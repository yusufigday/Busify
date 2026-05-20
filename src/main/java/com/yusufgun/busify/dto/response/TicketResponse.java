package com.yusufgun.busify.dto.response;

import com.yusufgun.busify.enums.Gender;

public record TicketResponse(
        Long id,
        Long routeId,
        String userTcNo,
        int seatNumber,
        Gender gender,
        Double price
) {}