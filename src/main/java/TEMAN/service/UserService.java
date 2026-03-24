package TEMAN.service;

import TEMAN.domain.User;
import TEMAN.domain.enums.ProviderEnum;
import TEMAN.domain.enums.RoleEnum;
import TEMAN.dto.request.*;
import TEMAN.dto.response.SocialLoginResponseDto;
import TEMAN.repository.UserRepository;
import TEMAN.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    public Long signup(UserSignupRequestDto userSignupRequestDto) {
        //이메일 중복 체크
        if(userRepository.existsByEmail(userSignupRequestDto.email())) {
            throw new IllegalArgumentException("This email is already in use. ");
        }

        //로그인 아이디 중복 체크
        if(userRepository.existsByLoginId(userSignupRequestDto.loginId())) {
            throw new IllegalArgumentException("This ID is already in use. ");
        }

        //비밀번호 암호화 <- 스프링 시큐리티
        String encodedPassword = passwordEncoder.encode(userSignupRequestDto.password());

        //Dto -> Entity 변환
        //회원가입 시 사진 넣지 않기 때문에 사진은 없음
        User user = User.builder()
                .email(userSignupRequestDto.email())
                .loginId(userSignupRequestDto.loginId())
                .fullName(userSignupRequestDto.fullName())
                .password(encodedPassword)
                .age(userSignupRequestDto.age())
                .countryEnum(userSignupRequestDto.countryEnum())
                .phone(userSignupRequestDto.phone())
                .roleEnum(RoleEnum.USER)
                .isOriginalUser(false)
                .providerEnum(ProviderEnum.LOCAL)
                .socialId(null)
                .build();

        //관심사 업데이트
        user.updateInterests(userSignupRequestDto.interests());

        //db 저장
        User savedUser = userRepository.save(user);

        //pk 반환
        return savedUser.getId();
    }

    public String login(UserLoginRequestDto userLoginRequestDto) {
        // 이메일로 유저 찾기
        User user = userRepository.findUserByEmail(userLoginRequestDto.email())
                .orElseThrow(() -> new IllegalArgumentException("This is an unregistered email. "));

        //소셜 가입자인지 확인
        if(user.getProviderEnum() != ProviderEnum.LOCAL) {
            throw new IllegalArgumentException("You are a user registered with a " + user.getProviderEnum() + "account. Please use social login.");
        }

        //비밀번호 일치 확인
        if(!passwordEncoder.matches(userLoginRequestDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("The passwords do not match.");
        }

        //성공 시 JWT 토큰 발급
        return jwtUtil.createAccessToken(user.getEmail(), user.getRoleEnum());
    }

    public String findLoginId(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("This email does not exist."));

        if(user.isOriginalUser()) {
            throw new IllegalArgumentException("Members of the previous version cannot use the ID search function.");
        }

        if(user.getProviderEnum() != ProviderEnum.LOCAL) {
            throw new IllegalArgumentException("This email is linked to the " + user.getProviderEnum() + " account. ");
        }

        return user.getLoginId();
    }

    public SocialLoginResponseDto socialLogin(SocialLoginRequestDto socialLoginRequestDto) {
        String email = "";
        String socialId = "";

        // 프론트엔드에서 발급받은 Google Web Client ID
        String GOOGLE_CLIENT_ID = "여기에_실제_구글_클라이언트_ID";

        if (socialLoginRequestDto.provider() == TEMAN.domain.enums.ProviderEnum.GOOGLE) {
            try {
                // 1. 구글 공식 검증기 객체 생성
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                        .build();

                // 2. 프론트가 보낸 토큰 검증
                GoogleIdToken idToken = verifier.verify(socialLoginRequestDto.token());

                if (idToken != null) {
                    // 3. 진짜 토큰이 맞다면 내부 정보(Payload) 꺼냄
                    GoogleIdToken.Payload payload = idToken.getPayload();
                    email = payload.getEmail();
                    socialId = payload.getSubject();
                } else {
                    throw new IllegalArgumentException("This is an invalid Google ID token.");
                }
            } catch (Exception e) {
                // 예외가 터지면 여기서 멈춤
                throw new IllegalArgumentException("An error occurred during social token verification: " + e.getMessage());
            }

        } else if (socialLoginRequestDto.provider() == TEMAN.domain.enums.ProviderEnum.APPLE) { //임시인 부분
            email = "apple_mock_email@icloud.com";
            socialId = "apple_mock_123456";
        }
        // 검증 종료 후
        // 2. DB에서 이메일로 기존 가입자인지 확인
        var optionalUser = userRepository.findUserByEmail(email);

        // 3-A. 신규 유저인 경우 (가입 창으로 보내기)
        if (optionalUser.isEmpty()) {
            return new SocialLoginResponseDto(true, email, socialId, null);
        }

        User user = optionalUser.get();

        // 3-B. 기존 유저지만, 다른 방식으로 가입한 경우 방어
        if (user.getProviderEnum() != socialLoginRequestDto.provider()) {
            throw new IllegalArgumentException("This email address is already registered in the " + user.getProviderEnum() + " method. ");
        }

        // 4. 로그인 성공 -> 진짜 JWT 발급
        String jwtToken = jwtUtil.createAccessToken(user.getEmail(), user.getRoleEnum());
        return new SocialLoginResponseDto(false, user.getEmail(), user.getSocialId(), jwtToken);
    }

    @Transactional
    public Long socialSignup(SocialSignupRequestDto socialSignupRequestDto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(socialSignupRequestDto.email())) {
            throw new IllegalArgumentException("This email address is already registered.");
        }

        // 2. 로그인 아이디 중복 체크
        if (userRepository.existsByLoginId(socialSignupRequestDto.loginId())) {
            throw new IllegalArgumentException("This ID is already in use.");
        }

        // 3. DTO -> Entity 변환
        User user = User.builder()
                .email(socialSignupRequestDto.email())
                .loginId(socialSignupRequestDto.loginId())
                .fullName(socialSignupRequestDto.fullName())
                .password(null) //소셜가입자 비밀번호 없음
                .age(socialSignupRequestDto.age())
                .countryEnum(socialSignupRequestDto.countryEnum())
                .phone(socialSignupRequestDto.phone())
                .roleEnum(RoleEnum.USER)
                .isOriginalUser(false)
                // 소셜 가입자 전용 데이터
                .providerEnum(socialSignupRequestDto.provider())
                .socialId(socialSignupRequestDto.socialId())
                .build();

        // 4. 관심사 업데이트
        user.updateInterests(socialSignupRequestDto.interests());

        // 5. DB 저장
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    @Transactional
    public void sendPasswordResetLink(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("This is an unregistered email. "));

        if (user.getProviderEnum() != ProviderEnum.LOCAL) {
            throw new IllegalArgumentException(user.getProviderEnum() + " social linked account. ");
        }

        // 1. 유일한 토큰 생성
        String resetToken = UUID.randomUUID().toString();

        // 2. Redis에 저장 (Key: 토큰, Value: 이메일, 유효시간: 5분)
        redisTemplate.opsForValue().set(
                "PWD_RESET:" + resetToken,
                email,
                Duration.ofMinutes(5)
        );

        // 3. 리셋 링크 생성 (실제 프론트엔드 URL로 변경해야 함)
        // 프론트엔드 라우팅 주소 예시: https://teman-app.com/reset-password?token=어쩌구저쩌구
        String resetLink = "https://프론트엔드_도메인주소/reset-password?token=" + resetToken;

        // 4. 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[TEMAN] Password Reset Link Guide");
        message.setText("Hello, this is TEMAN.\n\n" +
                "Please click the link below to set a new password.\n" +
                "This link is valid for only 5 minutes for security purposes.\n\n" +
                resetLink);

        javaMailSender.send(message);
    }

    @Transactional
    public void resetPassword(PasswordResetRequestDto requestDto) {
        // 1. Redis에서 토큰 조회
        String email = redisTemplate.opsForValue().get("PWD_RESET:" + requestDto.token());

        if (email == null) {
            throw new IllegalArgumentException("This is an invalid or expired password reset link.");
        }

        // 2. 유저 찾기 및 비밀번호 업데이트
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("The user cannot be found."));

        user.updatePassword(passwordEncoder.encode(requestDto.newPassword()));

        // 3. 사용 완료된 토큰 파기 (1회용 보장)
        redisTemplate.delete("PWD_RESET:" + requestDto.token());
    }



}
