package TEMAN.dto.request;

import TEMAN.domain.enums.CountryEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserSignupRequestDto(

        @NotBlank(message = "Email is required.")
        @Email(message = "This is not a valid email format.")
        String email,

        @NotBlank(message = "Id is required.")
        String loginId,

        @NotBlank(message = "Name is required.")
        String fullName,

        @NotBlank(message = "Password is required.")
        @Size(min = 6, message = "The password must be at least 6 characters long.")
        String password,

        Integer age,

        @NotNull(message = "Please select your nationality.")
        CountryEnum countryEnum,

        String phone,

        List<String> interests

) {}