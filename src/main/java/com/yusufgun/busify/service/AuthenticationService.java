package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.AuthenticationRequest;
import com.yusufgun.busify.dto.request.RegisterRequest;
import com.yusufgun.busify.dto.response.AuthenticationResponse;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.repository.UserRepository;
import com.yusufgun.busify.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }
        if (repository.existsByTcNo(request.tcNo())) {
            throw new IllegalArgumentException("Error: TC No is already in use!");
        }

        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setTcNo(request.tcNo());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = repository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found with this email"));

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }
}