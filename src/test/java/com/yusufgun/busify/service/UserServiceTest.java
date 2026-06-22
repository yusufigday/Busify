package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.UserUpdateRequest;
import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.UserMapper;
import com.yusufgun.busify.repository.TicketRepository;
import com.yusufgun.busify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

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

        userResponse = new UserResponse(1L, "Yusuf", "Gun", "yusuf@test.com", "12345678901");
        updateRequest = new UserUpdateRequest("Yusuf", "Gun", "yusuf@test.com", "newPassword123");
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void getAllUsers_success() {
            when(userRepository.findAll()).thenReturn(List.of(user));
            when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).firstName()).isEqualTo("Yusuf");
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void getAllUsers_emptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUser Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user by tcNo")
        void getUser_success() {
            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(userMapper.toUserResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.getUser("12345678901");

            assertThat(result).isNotNull();
            assertThat(result.tcNo()).isEqualTo("12345678901");
            assertThat(result.firstName()).isEqualTo("Yusuf");
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUser_notFound() {
            when(userRepository.findByTcNo(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser("99999999999"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("updateUser Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void updateUser_success() {
            UserUpdateRequest request = new UserUpdateRequest("Ahmet", "Yilmaz", "ahmet@test.com", "newPass");
            UserResponse updatedResponse = new UserResponse(1L, "Ahmet", "Yilmaz", "ahmet@test.com", "12345678901");

            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("ahmet@test.com")).thenReturn(false);
            when(passwordEncoder.encode("newPass")).thenReturn("encoded_new_pass");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(any(User.class))).thenReturn(updatedResponse);

            UserResponse result = userService.updateUser("12345678901", request);

            assertThat(result.firstName()).isEqualTo("Ahmet");
            assertThat(result.email()).isEqualTo("ahmet@test.com");
            verify(passwordEncoder).encode("newPass");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should update user when email unchanged")
        void updateUser_sameEmail() {
            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

            UserResponse result = userService.updateUser("12345678901", updateRequest);

            assertThat(result).isNotNull();
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Should throw exception when new email already exists")
        void updateUser_emailAlreadyExists() {
            UserUpdateRequest request = new UserUpdateRequest("Yusuf", "Gun", "taken@test.com", "pass");

            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser("12345678901", request))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Email already exists");
        }

        @Test
        @DisplayName("Should throw exception when user not found for update")
        void updateUser_notFound() {
            when(userRepository.findByTcNo(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser("99999999999", updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteUser Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void deleteUser_success() {
            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(ticketRepository.existsByUserTcNo("12345678901")).thenReturn(false);

            userService.deleteUser("12345678901");

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Should throw exception when user has purchased tickets")
        void deleteUser_hasTickets() {
            when(userRepository.findByTcNo("12345678901")).thenReturn(Optional.of(user));
            when(ticketRepository.existsByUserTcNo("12345678901")).thenReturn(true);

            assertThatThrownBy(() -> userService.deleteUser("12345678901"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("purchased tickets");

            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found for delete")
        void deleteUser_notFound() {
            when(userRepository.findByTcNo(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser("99999999999"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateUserRole Tests")
    class UpdateUserRoleTests {

        @Test
        @DisplayName("Should update user role successfully")
        void updateUserRole_success() {
            when(userRepository.findByEmail("yusuf@test.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            String result = userService.updateUserRole("yusuf@test.com", Role.STAFF);

            assertThat(result).contains("successfully updated");
            assertThat(result).contains("STAFF");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found for role update")
        void updateUserRole_userNotFound() {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUserRole("unknown@test.com", Role.ADMIN))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }
}
