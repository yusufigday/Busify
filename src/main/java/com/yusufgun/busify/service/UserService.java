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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TicketRepository ticketRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(String tcNo) {
        String cleanTcNo = tcNo.trim();
        User user = userRepository.findByTcNo(cleanTcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + cleanTcNo));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String tcNo, UserUpdateRequest updatedUser) {
        String cleanTcNo = tcNo.trim();
        User user = userRepository.findByTcNo(cleanTcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + cleanTcNo));

        String requestedEmail = updatedUser.email().trim().toLowerCase();
        String currentEmail = user.getEmail().trim().toLowerCase();

        if (!currentEmail.equals(requestedEmail) && userRepository.existsByEmail(requestedEmail)) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        user.setFirstName(updatedUser.firstName().trim());
        user.setLastName(updatedUser.lastName().trim());
        user.setEmail(requestedEmail);
        user.setPassword(updatedUser.password());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String tcNo) {
        String cleanTcNo = tcNo.trim();
        User user = userRepository.findByTcNo(cleanTcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + cleanTcNo));

        if (ticketRepository.existsByUserTcNo(cleanTcNo)) {
            throw new IllegalStateException("Cannot delete user! This user has purchased tickets. Cancel the tickets first.");
        }

        userRepository.delete(user);
    }

    public String updateUserRole(String email, Role newRole){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email: " + email));

        user.setRole(newRole);

        userRepository.save(user);

        return "Role of user " + user.getFirstName() + " " + user.getLastName() + " has been successfully to: " + newRole.name();
    }
}