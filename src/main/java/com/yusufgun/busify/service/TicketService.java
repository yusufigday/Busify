package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.entity.Ticket;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
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

    public TicketResponse buyTicket(TicketRequest ticketRequest) {

        Route route = routeRepository.findById(ticketRequest.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + ticketRequest.getRouteId()));

        User user = userRepository.findByTcNo(ticketRequest.getTcNo())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ticketRequest.getTcNo()));

        if (ticketRequest.getSeatNumber() > route.getBus().getCapacity()) {
            throw new IllegalArgumentException("Seat number cannot exceed bus capacity");
        }

        int soldTicketCount = ticketRepository.findByRouteId(route.getId()).size();
        if (soldTicketCount >= route.getBus().getCapacity()) {
            throw new RuntimeException("Sorry, this bus is completely full!");
        }

        if (ticketRepository.existsByRouteIdAndSeatNumber(route.getId(), ticketRequest.getSeatNumber())) {
            throw new ResourceAlreadyExistsException("Seat Number: " + ticketRequest.getSeatNumber() + " is already taken!");
        }

        Ticket ticket = new Ticket();
        ticket.setRoute(route);
        ticket.setUser(user);
        ticket.setSeatNumber(ticketRequest.getSeatNumber());
        ticket.setGender(ticketRequest.getGender());

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(savedTicket.getId());
        ticketResponse.setRouteId(route.getId());
        ticketResponse.setUserTcNo(user.getTcNo());
        ticketResponse.setSeatNumber(savedTicket.getSeatNumber());
        ticketResponse.setGender(savedTicket.getGender());
        ticketResponse.setPrice(route.getPrice());

        return ticketResponse;
    }

    public List<SeatInfoResponse> getSeatMap(Long routeId) {

        List<Ticket> tickets = ticketRepository.findByRouteId(routeId);

        return tickets.stream()
                .map(ticket -> new SeatInfoResponse(ticket.getSeatNumber(), ticket.getGender()))
                .collect(Collectors.toList());
    }
}
