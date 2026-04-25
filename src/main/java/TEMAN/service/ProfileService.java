package TEMAN.service;

import TEMAN.domain.User;
import TEMAN.dto.request.ProfileUpdateRequestDto;
import TEMAN.dto.response.ProfileResponseDto;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 내 프로필 조회
    @Transactional(readOnly = true)
    public ProfileResponseDto getMyProfile(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));
        return ProfileResponseDto.fromEntity(user);
    }

    // 내 프로필 수정
    public void updateProfile(String email, ProfileUpdateRequestDto profileUpdateRequestDto, MultipartFile profileImage) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        String newProfileImageUrl = null;
        if(profileImage != null && !profileImage.isEmpty()) {
            newProfileImageUrl = s3Service.uploadImage(profileImage);
        }

        // 더티체킹 -> 업데이트
        user.updateProfile(
                profileUpdateRequestDto.fullName(),
                profileUpdateRequestDto.bio(),
                profileUpdateRequestDto.instagramId(),
                profileUpdateRequestDto.interests(),
                profileUpdateRequestDto.genderEnums(),
                profileUpdateRequestDto.showGender(),
                newProfileImageUrl
        );
    }
}
