package com.yusufgun.busify.dto.response;

import com.yusufgun.busify.enums.Gender;
import lombok.Data;

@Data
public class TicketResponse {
    private Long id;
    private Long routeId;
    private String userTcNo;
    private int seatNumber;
    private Gender gender;
    private Double price;
}
