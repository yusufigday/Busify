package com.yusufgun.busify.dto.response;

import com.yusufgun.busify.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatInfoResponse {
    private int seatNumber;
    private Gender gender;
}
