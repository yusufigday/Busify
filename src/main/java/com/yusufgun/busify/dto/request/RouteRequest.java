package com.yusufgun.busify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record RouteRequest(
        @NotBlank(message = "Origin cannot be blank")
        String origin,

        @NotBlank(message = "Destination cannot be blank")
        String destination,

        @NotNull(message = "Departure date cannot be null")
        @FutureOrPresent(message = "Departure date cannot be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate departureDate,

        @NotNull(message = "Departure time cannot be null")
        @JsonFormat(pattern = "HH:mm")
        LocalTime departureTime,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double price,

        @NotNull(message = "Bus ID cannot be null")
        Long busId
) {}