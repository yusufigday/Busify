package com.yusufgun.busify.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}