package TEMAN.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindIdRequestDto (
        @NotBlank(message = "Please enter your email address.")
        @Email(message = "This is not a valid email format. ")
        String email
) {
}
