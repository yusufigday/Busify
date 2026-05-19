package com.yusufgun.busify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

}
