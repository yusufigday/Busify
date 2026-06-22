package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.entity.Ticket;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.TicketMapper;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import com.yusufgun.busify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponse buyTicket(TicketRequest ticketRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Route route = routeRepository.findById(ticketRequest.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + ticketRequest.routeId()));

        if (ticketRequest.seatNumber() > route.getBus().getCapacity()) {
            throw new IllegalArgumentException("Seat number cannot exceed bus capacity");
        }

        int soldTicketCount = ticketRepository.findByRouteId(route.getId()).size();
        if (soldTicketCount >= route.getBus().getCapacity()) {
            throw new IllegalStateException("Sorry, this bus is completely full!");
        }

        if (ticketRepository.existsByRouteIdAndSeatNumber(route.getId(), ticketRequest.seatNumber())) {
            throw new ResourceAlreadyExistsException("Seat Number: " + ticketRequest.seatNumber() + " is already taken!");
        }

        Ticket ticket = new Ticket();
        ticket.setRoute(route);
        ticket.setUser(currentUser);
        ticket.setSeatNumber(ticketRequest.seatNumber());
        ticket.setGender(ticketRequest.gender());

        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toTicketResponse(savedTicket);
    }

    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isOwner = ticket.getUser().getTcNo().equals(currentUser.getTcNo());
        boolean isAuthorizedStaff = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.STAFF;

        if (!isOwner && !isAuthorizedStaff) {
            throw new IllegalStateException("Security Breach: You are not authorized to view this ticket!");
        }

        return ticketMapper.toTicketResponse(ticket);
    }

    public List<SeatInfoResponse> getSeatMap(Long routeId) {
        return ticketRepository.findByRouteId(routeId).stream()
                .map(ticket -> new SeatInfoResponse(ticket.getSeatNumber(), ticket.getGender()))
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getUserTickets(String userTcNo) {
        String cleanTcNo = userTcNo.trim();

        if (!userRepository.existsByTcNo(cleanTcNo)) {
            throw new ResourceNotFoundException("User not found with tcNo: " + cleanTcNo);
        }

        return ticketRepository.findByUserTcNo(cleanTcNo).stream()
                .map(ticketMapper::toTicketResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isOwner = ticket.getUser().getTcNo().equals(currentUser.getTcNo());
        boolean isAuthorizedStaff = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.STAFF;

        if (!isOwner && !isAuthorizedStaff) {
            throw new IllegalStateException("Security Breach: You are not authorized to cancel this ticket!");
        }

        ticketRepository.delete(ticket);
    }
}