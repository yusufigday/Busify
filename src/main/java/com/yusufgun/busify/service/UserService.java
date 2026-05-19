package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.RegisterRequest;
import com.yusufgun.busify.dto.UserResponse;
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
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByTcNo(request.getTcNo())) {
            throw new ResourceAlreadyExistsException("TC No already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setTcNo(request.getTcNo());

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

        if (!user.getEmail().equals(updatedUser.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
           throw new ResourceAlreadyExistsException("Email already exists");
        }

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());

        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUser(String tcNo) {
        User user = userRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this TC No: " + tcNo));

        userRepository.delete(user);
    }
}
