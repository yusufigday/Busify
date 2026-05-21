package com.yusufgun.busify.dto.request;

import java.time.LocalDate;

public record RouteSearchRequest(
        String origin,
        String destination,
        LocalDate date
) {}
