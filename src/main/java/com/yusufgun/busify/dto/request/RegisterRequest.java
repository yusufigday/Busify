package com.yusufgun.busify.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @Email(message = "Please provide a valid email address")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "National ID cannot be blank")
        @Pattern(regexp = "^[0-9]{11}$", message = "National ID must consist of exactly 11 digits")
        String tcNo,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}