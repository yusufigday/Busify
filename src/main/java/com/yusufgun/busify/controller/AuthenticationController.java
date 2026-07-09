package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.AuthenticationRequest;
import com.yusufgun.busify.dto.request.RegisterRequest;
import com.yusufgun.busify.dto.response.AuthenticationResponse;
import com.yusufgun.busify.security.JwtService;
import com.yusufgun.busify.service.AuthenticationService;
import com.yusufgun.busify.service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    @com.yusufgun.busify.annotation.RateLimited(limit = 5, duration = 60)
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    @com.yusufgun.busify.annotation.RateLimited(limit = 5, duration = 60)
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long remainingTime = jwtService.getExpirationTime(token) - System.currentTimeMillis();
            if (remainingTime > 0) {
                tokenBlacklistService.blacklistToken(token, remainingTime);
            }
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}