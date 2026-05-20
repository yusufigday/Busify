package com.yusufgun.busify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RouteRequest(
        String origin,
        String destination,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate departureDate,

        @JsonFormat(pattern = "HH:mm")
        LocalTime departureTime,

        Double price,

        @NotNull(message = "Bus ID cannot be null")
        Long busId
) {}