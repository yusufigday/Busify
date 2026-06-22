package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.AuthenticationRequest;
import com.yusufgun.busify.dto.request.RegisterRequest;
import com.yusufgun.busify.dto.response.AuthenticationResponse;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.repository.UserRepository;
import com.yusufgun.busify.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Yusuf");
        user.setLastName("Gun");
        user.setEmail("yusuf@test.com");
        user.setTcNo("12345678901");
        user.setPassword("encoded_password");
        user.setRole(Role.USER);

        registerRequest = new RegisterRequest("Yusuf", "Gun", "yusuf@test.com", "12345678901", "password123");
        authRequest = new AuthenticationRequest("yusuf@test.com", "password123");
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully")
        void register_success() {
            when(repository.existsByEmail("yusuf@test.com")).thenReturn(false);
            when(repository.existsByTcNo("12345678901")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
            when(repository.save(any(User.class))).thenReturn(user);
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token-123");

            AuthenticationResponse result = authenticationService.register(registerRequest);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token-123");
            verify(repository).save(any(User.class));
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("Should throw exception when email already in use")
        void register_emailAlreadyExists() {
            when(repository.existsByEmail("yusuf@test.com")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(registerRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email is already in use");

            verify(repository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when TC No already in use")
        void register_tcNoAlreadyExists() {
            when(repository.existsByEmail("yusuf@test.com")).thenReturn(false);
            when(repository.existsByTcNo("12345678901")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(registerRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TC No is already in use");

            verify(repository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("authenticate Tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should authenticate user successfully")
        void authenticate_success() {
            when(repository.findByEmail("yusuf@test.com")).thenReturn(Optional.of(user));
            when(jwtService.generateToken(user)).thenReturn("jwt-token-456");

            AuthenticationResponse result = authenticationService.authenticate(authRequest);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token-456");
            verify(authenticationManager).authenticate(
                    any(UsernamePasswordAuthenticationToken.class)
            );
        }

        @Test
        @DisplayName("Should throw exception when user not found during authentication")
        void authenticate_userNotFound() {
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.authenticate(authRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }
    }
}
