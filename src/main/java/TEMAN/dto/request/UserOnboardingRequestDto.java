package TEMAN.dto.request;

import TEMAN.domain.enums.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record UserOnboardingRequestDto (
        @NotNull(message = "You must agree to the terms.")
        Boolean agreeTerms,

        @NotBlank(message = "Please enter your nickname. ")
        String fullName,

        @NotNull(message = "Please enter your birthday. ")
        LocalDate birthday,

        List<GenderEnum> genders,
        Boolean showGender,
        String bio,
        String instagramId,
        List<String> interests
) {
}
