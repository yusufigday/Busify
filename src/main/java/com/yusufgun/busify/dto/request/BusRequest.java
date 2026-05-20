package com.yusufgun.busify.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusRequest(

    @NotBlank(message = "Plate number cannot be blank")
    String plate,

    @Min(value = 10, message = "Capacity must be at least 10")
    int capacity,

    @NotNull(message = "Company ID cannot be null")
    Long companyId
) {}
