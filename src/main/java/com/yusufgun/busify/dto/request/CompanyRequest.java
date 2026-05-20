package com.yusufgun.busify.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(

        @NotBlank(message = "Company name cannot be blank")
        String name,

        @NotBlank(message = "Contact number cannot be blank")
        String contactNumber

) {}
