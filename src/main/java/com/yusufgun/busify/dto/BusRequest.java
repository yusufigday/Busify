package com.yusufgun.busify.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BusRequest {

    @NotBlank(message = "Plate number cannot be blank")
    private String plate;

    @Min(value = 10, message = "Capacity must be at least 10")
    private int capacity;

    @NotNull(message = "Company ID cannot be null")
    private Long companyId;
}
