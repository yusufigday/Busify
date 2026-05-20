package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.entity.Ticket;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.TicketMapper;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import com.yusufgun.busify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketResponse buyTicket(TicketRequest ticketRequest) {

        Route route = routeRepository.findById(ticketRequest.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + ticketRequest.routeId()));

        User user = userRepository.findByTcNo(ticketRequest.tcNo())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ticketRequest.tcNo()));

        if (ticketRequest.seatNumber() > route.getBus().getCapacity()) {
            throw new IllegalArgumentException("Seat number cannot exceed bus capacity");
        }

        int soldTicketCount = ticketRepository.findByRouteId(route.getId()).size();
        if (soldTicketCount >= route.getBus().getCapacity()) {
            throw new RuntimeException("Sorry, this bus is completely full!");
        }

        if (ticketRepository.existsByRouteIdAndSeatNumber(route.getId(), ticketRequest.seatNumber())) {
            throw new ResourceAlreadyExistsException("Seat Number: " + ticketRequest.seatNumber() + " is already taken!");
        }

        Ticket ticket = new Ticket();
        ticket.setRoute(route);
        ticket.setUser(user);
        ticket.setSeatNumber(ticketRequest.seatNumber());
        ticket.setGender(ticketRequest.gender());

        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toTicketResponse(savedTicket);
    }

    public List<SeatInfoResponse> getSeatMap(Long routeId) {
        List<Ticket> tickets = ticketRepository.findByRouteId(routeId);
        return tickets.stream()
                .map(ticket -> new SeatInfoResponse(ticket.getSeatNumber(), ticket.getGender()))
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getUserTickets(String userTcNo) {
        if (!userRepository.existsByTcNo(userTcNo)) {
            throw new ResourceNotFoundException("User not found with tcNo: " + userTcNo);
        }

        List<Ticket> tickets = ticketRepository.findByUserTcNo(userTcNo);

        return tickets.stream().map(ticketMapper::toTicketResponse)
                .collect(Collectors.toList());
    }
}