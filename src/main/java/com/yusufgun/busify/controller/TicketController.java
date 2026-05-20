package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/buy")
    public ResponseEntity<TicketResponse> buyTicket(@Valid @RequestBody TicketRequest ticketRequest){
        return ResponseEntity.ok(ticketService.buyTicket(ticketRequest));
    }

    @GetMapping("/seat-map/{routeId}")
    public ResponseEntity<List<SeatInfoResponse>> getSeatMap(@PathVariable Long routeId){
        return ResponseEntity.ok(ticketService.getSeatMap(routeId));
    }
}
