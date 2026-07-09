package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/buy")
    @com.yusufgun.busify.annotation.RateLimited(limit = 10, duration = 60)
    public ResponseEntity<TicketResponse> buyTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        return ResponseEntity.ok(ticketService.buyTicket(ticketRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/user/{tcNo}")
    @PreAuthorize("#tcNo == authentication.principal.tcNo or hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<TicketResponse>> getUserTickets(@PathVariable String tcNo) {
        return ResponseEntity.ok(ticketService.getUserTickets(tcNo));
    }

    @GetMapping("/route/{routeId}/seats")
    public ResponseEntity<List<SeatInfoResponse>> getSeatMap(@PathVariable Long routeId) {
        return ResponseEntity.ok(ticketService.getSeatMap(routeId));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelTicket(@PathVariable Long id) {
        ticketService.cancelTicket(id);
        return ResponseEntity.noContent().build();
    }
}
