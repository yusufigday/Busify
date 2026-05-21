package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.RegisterRequest;
import com.yusufgun.busify.dto.request.UserUpdateRequest;
import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{tcNo}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String tcNo){
        return ResponseEntity.ok(userService.getUser(tcNo));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PutMapping("/update/{tcNo}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String tcNo,@Valid @RequestBody UserUpdateRequest updatedUser) {
        return ResponseEntity.ok(userService.updateUser(tcNo, updatedUser));
    }

    @DeleteMapping("/delete/{tcNo}")
    public ResponseEntity<Void> deleteUser(@PathVariable String tcNo) {
        userService.deleteUser(tcNo);
        return ResponseEntity.noContent().build();
    }
}
