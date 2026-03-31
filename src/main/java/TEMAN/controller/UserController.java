package TEMAN.controller;

import TEMAN.dto.request.*;
import TEMAN.dto.response.SocialLoginResponseDto;
import TEMAN.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login/social")
    public ResponseEntity<SocialLoginResponseDto> socialLogin(@Valid @RequestBody SocialLoginRequestDto socialLoginRequestDto) {
        SocialLoginResponseDto socialLoginResponseDto = userService.socialLogin(socialLoginRequestDto);

        //약관 동의 X -> 202 Accepted 반환, 약관 동의 화면으로 이동(프론트)
        if(socialLoginResponseDto.isNewUser()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(socialLoginResponseDto);
        }
        //이미 약관 동의 유저면 200 OK -> 바로 홈화면(프론트)
        return ResponseEntity.ok(socialLoginResponseDto);
    }

    @PatchMapping("/me/onboarding")
    public ResponseEntity<String> completeOnboarding(Principal principal, @Valid @RequestBody UserOnboardingRequestDto userOnboardingRequestDto) {
        String email = principal.getName();
        userService.completeOnboarding(email, userOnboardingRequestDto);

        return ResponseEntity.ok("Onboarding has been successfully completed. ");
    }
}
