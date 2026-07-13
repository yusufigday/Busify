package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.ChangeRoleRequest;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.service.TicketService;
import com.yusufgun.busify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final TicketService ticketService;

    @PutMapping("/change-role")
    public ResponseEntity<String> changeUserRole(@RequestBody ChangeRoleRequest changeRoleRequest)
    {
        return ResponseEntity.ok(userService.updateUserRole(changeRoleRequest.email(), changeRoleRequest.newRole()));
    }
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }
}