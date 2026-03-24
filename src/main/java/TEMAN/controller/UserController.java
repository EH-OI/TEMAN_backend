package TEMAN.controller;

import TEMAN.dto.request.*;
import TEMAN.dto.response.SocialLoginResponseDto;
import TEMAN.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequestDto userSignupRequestDto) {
        Long savedUserId = userService.signup(userSignupRequestDto);
        return ResponseEntity.ok(userService.findLoginId(userSignupRequestDto.email()));
    }

    @PostMapping("/login/social")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody SocialLoginRequestDto socialLoginRequestDto) {
        SocialLoginResponseDto socialLoginResponseDto = userService.socialLogin(socialLoginRequestDto);

        if(socialLoginResponseDto.isNewUser()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(socialLoginResponseDto);
        }
        return ResponseEntity.ok(socialLoginResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
        String token = userService.login(userLoginRequestDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup/social")
    public ResponseEntity<String> socialSignup(@Valid @RequestBody SocialSignupRequestDto requestDto) {
        // 서비스 단에 일반 가입과 비슷하지만 비밀번호가 없는 socialSignup 로직이 필요합니다.
        Long savedUserId = userService.socialSignup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Social sign-up is complete. (User ID: " + savedUserId + ")");
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<String> findLoginId(@Valid @RequestBody FindIdRequestDto requestDto) {
        String loginId = userService.findLoginId(requestDto.email());
        // 프론트에서 파싱하기 쉽게 JSON 규격으로 내려주면 더 좋습니다.
        return ResponseEntity.ok("Your ID is [" + loginId + "].");
    }

    // 비밀번호 리셋 링크 전송 API
    @PostMapping("/password/reset-link")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody FindIdRequestDto requestDto) {
        userService.sendPasswordResetLink(requestDto.email());
        return ResponseEntity.ok("We have sent a password reset link to the email address you entered.");
    }

    // 실제 비밀번호 변경 수행 API
    @PostMapping("/password/reset")
    public ResponseEntity<String> confirmPasswordReset(@Valid @RequestBody PasswordResetRequestDto requestDto) {
        userService.resetPassword(requestDto);
        return ResponseEntity.ok("The password has been successfully changed.");
    }
}
