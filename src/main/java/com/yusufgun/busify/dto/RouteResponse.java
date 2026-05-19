package com.yusufgun.busify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RouteResponse {

    private Long id;

    private String origin;
    private String destination;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    private Double price;


}
