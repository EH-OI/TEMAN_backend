package TEMAN.controller;

import TEMAN.dto.request.ProfileUpdateRequestDto;
import TEMAN.dto.response.ProfileResponseDto;
import TEMAN.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponseDto> getMyProfile(Principal principal) {
        ProfileResponseDto profileResponseDto = profileService.getMyProfile(principal.getName());
        return ResponseEntity.ok(profileResponseDto);
    }

    @PutMapping
    public ResponseEntity<String> updateProfile(Principal principal, @RequestPart(value = "data") @Valid ProfileUpdateRequestDto profileUpdateRequestDto,
                                                @RequestPart(value = "image", required = false)MultipartFile profileImage) {

        profileService.updateProfile(principal.getName(), profileUpdateRequestDto, profileImage);
        return ResponseEntity.ok("프로필이 수정되었습니다. ");
    }
}
