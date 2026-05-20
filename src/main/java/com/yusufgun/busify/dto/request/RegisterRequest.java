package com.yusufgun.busify.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "National ID cannot be blank")
    @Pattern(regexp = "^[0-9]{11}$", message = "National ID must consist of exactly 11 digits")
    private String tcNo;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
