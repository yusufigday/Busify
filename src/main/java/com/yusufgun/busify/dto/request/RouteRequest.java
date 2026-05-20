package com.yusufgun.busify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RouteRequest {

    private String origin;
    private String destination;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    private Double price;

    @NotNull(message = "Bus ID cannot be null")
    private Long busId;

}
