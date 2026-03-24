package TEMAN.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequestDto (
        @NotBlank(message = "There is no authentication token. ")
        String token,

        @NotBlank(message = "Please enter a new password. ")
        @Size(min = 6, message = "The password must be at least 6 characters long. ")
        String newPassword
) {
}
