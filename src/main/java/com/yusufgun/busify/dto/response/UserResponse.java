package com.yusufgun.busify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yusufgun.busify.enums.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String tcNo,
        Role role
) {}