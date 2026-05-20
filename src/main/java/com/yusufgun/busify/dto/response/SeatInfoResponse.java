package com.yusufgun.busify.dto.response;

import com.yusufgun.busify.enums.Gender;

public record SeatInfoResponse(
        int seatNumber,
        Gender gender
) {}