package TEMAN.dto.request;

import TEMAN.domain.enums.ProviderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequestDto(
        @NotNull(message = "A social login provider is required.")
        ProviderEnum provider, // GOOGLE 또는 APPLE

        @NotBlank(message = "A social authentication token (idToken) is required.")
        String token
) {}