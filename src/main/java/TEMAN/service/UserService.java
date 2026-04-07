package TEMAN.service;

import TEMAN.domain.User;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    // 🔥 1단계: 구글 로그인 시 껍데기 유저를 만들고 바로 JWT 토큰 발급
    public SocialLoginResponseDto socialLogin(SocialLoginRequestDto socialLoginRequestDto) {
        String email = "";
        String socialId = "";
        String fullName = "Please enter your name.";

        if (socialLoginRequestDto.provider() == TEMAN.domain.enums.ProviderEnum.GOOGLE) {
            // 🔥 포스트맨 테스트용 임시 백도어 (프론트 연동할 때 지우면 됩니다!)
            if ("test_token".equals(socialLoginRequestDto.token())) {
                email = "test_google@gmail.com";
                socialId = "google_test_12345";
                fullName = "구글 테스트 유저";
            } else {
                // 기존의 깐깐한 구글 진짜 검증 로직
                try {
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                            .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                            .build();

                    GoogleIdToken idToken = verifier.verify(socialLoginRequestDto.token());

                    if (idToken != null) {
                        GoogleIdToken.Payload payload = idToken.getPayload();
                        email = payload.getEmail();
                        socialId = payload.getSubject();
                        if (payload.get("name") != null) {
                            fullName = (String) payload.get("name");
                        }
                    } else {
                        throw new IllegalArgumentException("This is an invalid Google ID token.");
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("An error occurred during social token verification: " + e.getMessage());
                }
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 로그인 방식입니다.");
        }

        var optionalUser = userRepository.findUserByEmail(email);

        // [신규 유저인 경우]
        if (optionalUser.isEmpty()) {
            String randomLoginId = "google_" + UUID.randomUUID().toString().substring(0, 8);

            User newUser = User.builder()
                    .email(email)
                    .loginId(randomLoginId)
                    .fullName(fullName)
                    .roleEnum(RoleEnum.USER)
                    .isOriginalUser(false)
                    .providerEnum(socialLoginRequestDto.provider())
                    .socialId(socialId)
                    .build();

            userRepository.save(newUser);
            String jwtToken = jwtUtil.createAccessToken(newUser.getEmail(), newUser.getRoleEnum());

            // 🌟 온보딩 화면으로 보내기 위해 무조건 true 반환!
            return new SocialLoginResponseDto(true, newUser.getEmail(), newUser.getSocialId(), jwtToken);
        }

        // [기존 유저인 경우]
        User user = optionalUser.get();
        if (user.getProviderEnum() != socialLoginRequestDto.provider()) {
            throw new IllegalArgumentException("This email address is already registered in the " + user.getProviderEnum() + " method.");
        }

        String jwtToken = jwtUtil.createAccessToken(user.getEmail(), user.getRoleEnum());

        // 🌟 기존 유저라도 약관동의(온보딩)를 안 마쳤으면 true 반환해서 온보딩 화면으로 보냄!
        boolean needsOnboarding = !Boolean.TRUE.equals(user.getAgreeTerms());
        return new SocialLoginResponseDto(needsOnboarding, user.getEmail(), user.getSocialId(), jwtToken);
    }

    // 🔥 2단계: 프론트엔드가 피그마 온보딩 마지막 화면에서 던져줄 데이터 업데이트
    @Transactional
    public void completeOnboarding(String email, UserOnboardingRequestDto requestDto) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        user.completeOnboarding(
                requestDto.fullName(),
                requestDto.birthday(),
                requestDto.genders(),
                requestDto.showGender(),
                requestDto.bio(),
                requestDto.instagramId(),
                requestDto.interests()
        );
    }
}