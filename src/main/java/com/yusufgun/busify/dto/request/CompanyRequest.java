package com.yusufgun.busify.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "Company name cannot be blank")
    private String name;

    @NotBlank(message = "Contact number cannot be blank")
    private String contactNumber;

}
