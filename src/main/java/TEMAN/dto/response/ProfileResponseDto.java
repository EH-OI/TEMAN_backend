package TEMAN.dto.response;

import TEMAN.domain.User;
import TEMAN.domain.enums.GenderEnum;

import java.time.LocalDate;
import java.util.List;

public record ProfileResponseDto (
        String email,
        String fullName,
        String profileImageUrl,
        String bio,
        String instagramId,
        List<String> interests,
        List<GenderEnum> genderEnums,
        Boolean showGender,
        LocalDate birthday
) {
    public static ProfileResponseDto fromEntity(User user) {
        return new ProfileResponseDto(
                user.getEmail(),
                user.getFullName(),
                user.getProfileImageUrl(),
                user.getBio(),
                user.getInstagramId(),
                user.getInterests(),
                user.getGenders(),
                user.getShowGender(),
                user.getBirthday()
        );
    }
}
