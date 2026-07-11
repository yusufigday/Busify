package com.yusufgun.busify.service;

import com.yusufgun.busify.annotation.RateLimited;
import com.yusufgun.busify.config.RabbitMQConfig;
import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.entity.Ticket;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.logging.ElasticsearchLogService;
import com.yusufgun.busify.mapper.TicketMapper;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import com.yusufgun.busify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final ElasticsearchLogService elasticsearchLogService;
    private final RabbitMQProducer rabbitMQProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    @Cacheable(value = "tickets", unless = "#result.isEmpty()")
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toTicketResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "tickets", allEntries = true)
    @RateLimited(limit = 10, duration = 60)
    public TicketResponse buyTicket(TicketRequest ticketRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Route route = routeRepository.findById(ticketRequest.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + ticketRequest.routeId()));

        String lockKey = "lock:route:" + route.getId() + ":seat:" + ticketRequest.seatNumber();

        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new IllegalStateException("Seat is currently being booked by another user. Please try again.");
        }

        try {

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

            Map<String, Object> ticketDetails = new HashMap<>();
            ticketDetails.put("userId", currentUser.getId());
            ticketDetails.put("userEmail", currentUser.getEmail());
            ticketDetails.put("routeId", route.getId());
            ticketDetails.put("origin", route.getOrigin());
            ticketDetails.put("destination", route.getDestination());
            ticketDetails.put("seatNumber", ticketRequest.seatNumber());
            ticketDetails.put("price", route.getPrice());
            ticketDetails.put("action", "TICKET_PURCHASED");

            elasticsearchLogService.sendLog("busify-events", "INFO",
                    "Ticket purchased by user: " + currentUser.getEmail() + " -> " +
                            route.getOrigin() + "-" + route.getDestination() + " Seat: " + ticketRequest.seatNumber(), ticketDetails);

            Map<String, Object> ticketMessage = new HashMap<>();
            ticketMessage.put("action", "TICKET_PURCHASED");
            ticketMessage.put("email", currentUser.getEmail());
            ticketMessage.put("ticketId", savedTicket.getId());
            ticketMessage.put("route", route.getOrigin() + " → " + route.getDestination());
            ticketMessage.put("seatNumber", ticketRequest.seatNumber());
            ticketMessage.put("price", route.getPrice());

            rabbitMQProducer.sendMessage(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_TICKET,
                    ticketMessage
            );

            if (cacheManager.getCache("seatMap") != null) {
                cacheManager.getCache("seatMap").evict(route.getId());
            }

            redisTemplate.opsForZSet().incrementScore("popularRoutes", String.valueOf(route.getId()), 1);

            return ticketMapper.toTicketResponse(savedTicket);
        } finally {
            redisTemplate.delete(lockKey);
        }
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

    @Cacheable(value = "seatMap", key = "#routeId")
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
    @CacheEvict(value = "tickets", allEntries = true)
    public void cancelTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isOwner = ticket.getUser().getTcNo().equals(currentUser.getTcNo());
        boolean isAuthorizedStaff = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.STAFF;

        if (!isOwner && !isAuthorizedStaff) {
            throw new IllegalStateException("Security Breach: You are not authorized to cancel this ticket!");
        }

        Map<String, Object> cancelMessage = new HashMap<>();
        cancelMessage.put("action", "TICKET_CANCELLED");
        cancelMessage.put("email", currentUser.getEmail());
        cancelMessage.put("ticketId", ticket.getId());

        rabbitMQProducer.sendMessage(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_TICKET,
                cancelMessage
        );

        if (cacheManager.getCache("seatMap") != null) {
            cacheManager.getCache("seatMap").evict(ticket.getRoute().getId());
        }

        redisTemplate.opsForZSet().incrementScore("popularRoutes", String.valueOf(ticket.getRoute().getId()), -1);

        ticketRepository.delete(ticket);
    }
}