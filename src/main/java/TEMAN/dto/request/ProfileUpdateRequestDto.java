package TEMAN.dto.request;

import TEMAN.domain.enums.GenderEnum;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProfileUpdateRequestDto (
        @NotBlank(message = "이름은 필수입니다.")
        String fullName,
        String bio,
        String instagramId,
        List<String> interests,
        List<GenderEnum> genderEnums,
        Boolean showGender
) {
}
