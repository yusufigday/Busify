package com.yusufgun.busify.dto.response;

import lombok.Data;

@Data
public class BusResponse {
    private Long id;
    private String plate;
    private int capacity;
    private String companyName;
}
