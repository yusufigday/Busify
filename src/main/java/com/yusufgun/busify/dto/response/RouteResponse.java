package com.yusufgun.busify.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record RouteResponse(
        Long id,
        String origin,
        String destination,
        LocalDate departureDate,
        LocalTime departureTime,
        Double price
) {}