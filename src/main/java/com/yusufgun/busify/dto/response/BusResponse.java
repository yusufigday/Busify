package com.yusufgun.busify.dto.response;

public record BusResponse(
        Long id,
        String plate,
        int capacity,
        Long companyId
) {}