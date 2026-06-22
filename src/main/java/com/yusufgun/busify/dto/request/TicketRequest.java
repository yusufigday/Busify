package com.yusufgun.busify.dto.request;

import com.yusufgun.busify.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @NotNull(message = "Route ID cannot be null")
        Long routeId,

        @NotNull(message = "Seat number cannot be null")
        @Min(value = 1, message = "Seat number must be at least 1")
        Integer seatNumber,

        @NotNull(message = "Gender cannot be null")
        Gender gender
) {}