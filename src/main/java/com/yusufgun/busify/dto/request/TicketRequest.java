package com.yusufgun.busify.dto.request;

import com.yusufgun.busify.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {

    @NotNull(message = "Route ID cannot be null")
    private Long routeId;

    @NotBlank(message = "TC No cannot be blank")
    private String tcNo;

    @Min(value = 1, message = "Seat number must be at least 1")
    private int seatNumber;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;
}
