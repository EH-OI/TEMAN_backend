package TEMAN.dto.request;

import TEMAN.domain.enums.CountryEnum;
import TEMAN.domain.enums.ProviderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SocialSignupRequestDto(
        @NotBlank @Email
        String email,

        @NotBlank
        String socialId, // 소셜 로그인 API에서 돌려줬던 그 ID를 다시 받음

        @NotNull
        ProviderEnum provider,

        @NotBlank
        String loginId,

        @NotBlank
        String fullName,

        Integer age,

        @NotNull
        CountryEnum countryEnum,

        String phone,

        List<String> interests
) {}