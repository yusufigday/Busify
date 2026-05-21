package com.yusufgun.busify.dto.response;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String tcNo
) {}