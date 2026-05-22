package com.yusufgun.busify.controller;


import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/secret-report")
    public ResponseEntity<String> getSecretReport(){
        return ResponseEntity.ok("Welcome Boss! All confidential financial and operational reports of the company are here.");
    }

    @PutMapping("/change-role")
    public ResponseEntity<String> changeUserRole(
            @RequestParam String email,
            @RequestParam Role newRole
    ){
        return ResponseEntity.ok(userService.updateUserRole(email, newRole));
    }
}
