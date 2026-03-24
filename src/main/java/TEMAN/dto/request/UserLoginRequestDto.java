package TEMAN.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto (
        @NotBlank(message = "Please enter your email address. ")
        String email,
        @NotBlank(message = "Please enter your password ")
        String password
) {
}
