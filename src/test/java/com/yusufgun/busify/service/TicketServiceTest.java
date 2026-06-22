package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.TicketRequest;
import com.yusufgun.busify.dto.response.SeatInfoResponse;
import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.*;
import com.yusufgun.busify.enums.Gender;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.TicketMapper;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import com.yusufgun.busify.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketService ticketService;

    private User currentUser;
    private User otherUser;
    private Bus bus;
    private Route route;
    private Ticket ticket;
    private TicketRequest ticketRequest;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setFirstName("Yusuf");
        currentUser.setLastName("Gun");
        currentUser.setEmail("yusuf@test.com");
        currentUser.setTcNo("12345678901");
        currentUser.setPassword("encoded_password");
        currentUser.setRole(Role.USER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setFirstName("Ali");
        otherUser.setLastName("Veli");
        otherUser.setEmail("ali@test.com");
        otherUser.setTcNo("98765432109");
        otherUser.setPassword("encoded_password");
        otherUser.setRole(Role.USER);

        Company company = new Company();
        company.setId(1L);
        company.setName("TEST COMPANY");

        bus = new Bus();
        bus.setId(1L);
        bus.setPlate("34 ABC 123");
        bus.setCapacity(45);
        bus.setCompany(company);

        route = new Route();
        route.setId(1L);
        route.setOrigin("ISTANBUL");
        route.setDestination("ANKARA");
        route.setDepartureDate(LocalDate.of(2026, 7, 15));
        route.setDepartureTime(LocalTime.of(10, 30));
        route.setPrice(250.0);
        route.setBus(bus);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSeatNumber(5);
        ticket.setGender(Gender.MALE);
        ticket.setUser(currentUser);
        ticket.setRoute(route);

        ticketRequest = new TicketRequest(1L, 5, Gender.MALE);
        ticketResponse = new TicketResponse(1L, 1L, "12345678901", 5, Gender.MALE, 250.0);

        // Set security context for authenticated user
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("buyTicket Tests")
    class BuyTicketTests {

        @Test
        @DisplayName("Should buy ticket successfully")
        void buyTicket_success() {
            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(ticketRepository.findByRouteId(1L)).thenReturn(List.of());
            when(ticketRepository.existsByRouteIdAndSeatNumber(1L, 5)).thenReturn(false);
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toTicketResponse(any(Ticket.class))).thenReturn(ticketResponse);

            TicketResponse result = ticketService.buyTicket(ticketRequest);

            assertThat(result).isNotNull();
            assertThat(result.seatNumber()).isEqualTo(5);
            assertThat(result.gender()).isEqualTo(Gender.MALE);
            assertThat(result.routeId()).isEqualTo(1L);
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when route not found")
        void buyTicket_routeNotFound() {
            when(routeRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.buyTicket(ticketRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Route not found");

            verify(ticketRepository, never()).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when seat exceeds capacity")
        void buyTicket_seatExceedsCapacity() {
            TicketRequest invalidRequest = new TicketRequest(1L, 50, Gender.MALE);

            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

            assertThatThrownBy(() -> ticketService.buyTicket(invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Seat number cannot exceed bus capacity");

            verify(ticketRepository, never()).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when bus is full")
        void buyTicket_busFull() {
            // Create 45 tickets to fill the bus
            List<Ticket> fullTickets = java.util.stream.IntStream.rangeClosed(1, 45)
                    .mapToObj(i -> {
                        Ticket t = new Ticket();
                        t.setId((long) i);
                        t.setSeatNumber(i);
                        return t;
                    })
                    .toList();

            TicketRequest newRequest = new TicketRequest(1L, 1, Gender.FEMALE);

            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(ticketRepository.findByRouteId(1L)).thenReturn(fullTickets);

            assertThatThrownBy(() -> ticketService.buyTicket(newRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("completely full");

            verify(ticketRepository, never()).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when seat already taken")
        void buyTicket_seatAlreadyTaken() {
            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(ticketRepository.findByRouteId(1L)).thenReturn(List.of());
            when(ticketRepository.existsByRouteIdAndSeatNumber(1L, 5)).thenReturn(true);

            assertThatThrownBy(() -> ticketService.buyTicket(ticketRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("already taken");

            verify(ticketRepository, never()).save(any(Ticket.class));
        }
    }

    @Nested
    @DisplayName("getTicketById Tests")
    class GetTicketByIdTests {

        @Test
        @DisplayName("Should return ticket when user is owner")
        void getTicketById_owner() {
            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketMapper.toTicketResponse(ticket)).thenReturn(ticketResponse);

            TicketResponse result = ticketService.getTicketById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return ticket when user is ADMIN")
        void getTicketById_admin() {
            // Set ticket owner to different user
            ticket.setUser(otherUser);

            // Set current user as ADMIN
            currentUser.setRole(Role.ADMIN);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(ticketMapper.toTicketResponse(ticket)).thenReturn(ticketResponse);

            TicketResponse result = ticketService.getTicketById(1L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when user is not authorized")
        void getTicketById_unauthorized() {
            ticket.setUser(otherUser);

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            assertThatThrownBy(() -> ticketService.getTicketById(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void getTicketById_notFound() {
            when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.getTicketById(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSeatMap Tests")
    class GetSeatMapTests {

        @Test
        @DisplayName("Should return seat map for route")
        void getSeatMap_success() {
            Ticket ticket2 = new Ticket();
            ticket2.setId(2L);
            ticket2.setSeatNumber(10);
            ticket2.setGender(Gender.FEMALE);

            when(ticketRepository.findByRouteId(1L)).thenReturn(List.of(ticket, ticket2));

            List<SeatInfoResponse> result = ticketService.getSeatMap(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).seatNumber()).isEqualTo(5);
            assertThat(result.get(0).gender()).isEqualTo(Gender.MALE);
            assertThat(result.get(1).seatNumber()).isEqualTo(10);
            assertThat(result.get(1).gender()).isEqualTo(Gender.FEMALE);
        }

        @Test
        @DisplayName("Should return empty seat map for route with no tickets")
        void getSeatMap_empty() {
            when(ticketRepository.findByRouteId(1L)).thenReturn(List.of());

            List<SeatInfoResponse> result = ticketService.getSeatMap(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUserTickets Tests")
    class GetUserTicketsTests {

        @Test
        @DisplayName("Should return user tickets by tcNo")
        void getUserTickets_success() {
            when(userRepository.existsByTcNo("12345678901")).thenReturn(true);
            when(ticketRepository.findByUserTcNo("12345678901")).thenReturn(List.of(ticket));
            when(ticketMapper.toTicketResponse(any(Ticket.class))).thenReturn(ticketResponse);

            List<TicketResponse> result = ticketService.getUserTickets("12345678901");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when user not found by tcNo")
        void getUserTickets_userNotFound() {
            when(userRepository.existsByTcNo("00000000000")).thenReturn(false);

            assertThatThrownBy(() -> ticketService.getUserTickets("00000000000"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("cancelTicket Tests")
    class CancelTicketTests {

        @Test
        @DisplayName("Should cancel ticket when user is owner")
        void cancelTicket_owner() {
            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            ticketService.cancelTicket(1L);

            verify(ticketRepository).delete(ticket);
        }

        @Test
        @DisplayName("Should cancel ticket when user is STAFF")
        void cancelTicket_staff() {
            ticket.setUser(otherUser);

            currentUser.setRole(Role.STAFF);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            ticketService.cancelTicket(1L);

            verify(ticketRepository).delete(ticket);
        }

        @Test
        @DisplayName("Should throw exception when user not authorized to cancel")
        void cancelTicket_unauthorized() {
            ticket.setUser(otherUser);

            when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            assertThatThrownBy(() -> ticketService.cancelTicket(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not authorized");

            verify(ticketRepository, never()).delete(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when ticket not found for cancel")
        void cancelTicket_notFound() {
            when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.cancelTicket(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
