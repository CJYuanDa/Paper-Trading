package com.example.paper.trading.dataTransferObject;

import jakarta.validation.constraints.*;

import java.util.Date;

public record UserDTO(
        @NotBlank(message = "Please provide first name")
        String firstName,

        @NotBlank(message = "Please provide last name")
        String lastName,

        @NotBlank(message = "Please provide email")
        @Email(message = "Invalid Email")
        String email,

        @NotBlank(message = "Please provide password")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password,

        @NotNull(message = "Please provide your birthday")
        @Past(message = "Date of birth must be in the past")
        Date dateOfBirth
) {
}
