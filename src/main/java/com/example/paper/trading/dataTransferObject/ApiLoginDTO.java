package com.example.paper.trading.dataTransferObject;

import jakarta.validation.constraints.*;

import java.util.Date;

public record ApiLoginDTO(

        @NotBlank(message = "Please provide email")
        @Email(message = "Invalid Email")
        String username,

        @NotBlank(message = "Please provide password")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password

) {
}
