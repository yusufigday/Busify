package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.RegisterRequest;
import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.UserMapper;
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

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByTcNo(request.tcNo())) {
            throw new ResourceAlreadyExistsException("TC No already exists");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setTcNo(request.tcNo());

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    public UserResponse getUser(String tcNo) {
        User user = userRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + tcNo));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String tcNo, RegisterRequest updatedUser) {
        User user = userRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + tcNo));

        if (!user.getEmail().equals(updatedUser.email()) && userRepository.existsByEmail(updatedUser.email())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        user.setFirstName(updatedUser.firstName());
        user.setLastName(updatedUser.lastName());
        user.setEmail(updatedUser.email());
        user.setPassword(updatedUser.password());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String tcNo) {
        User user = userRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + tcNo));

        userRepository.delete(user);
    }
}