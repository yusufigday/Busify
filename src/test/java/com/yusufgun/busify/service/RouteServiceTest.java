package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.RouteRequest;
import com.yusufgun.busify.dto.request.RouteSearchRequest;
import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.entity.Bus;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.RouteMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private BusRepository busRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private RouteService routeService;

    private Bus bus;
    private Route route;
    private RouteRequest routeRequest;
    private RouteResponse routeResponse;
    private LocalDate departureDate;
    private LocalTime departureTime;

    @BeforeEach
    void setUp() {
        departureDate = LocalDate.of(2026, 7, 15);
        departureTime = LocalTime.of(10, 30);

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
        route.setDepartureDate(departureDate);
        route.setDepartureTime(departureTime);
        route.setPrice(250.0);
        route.setBus(bus);

        routeRequest = new RouteRequest("Istanbul", "Ankara", departureDate, departureTime, 250.0, 1L);
        routeResponse = new RouteResponse(1L, "ISTANBUL", "ANKARA", departureDate, departureTime, 250.0, 1L);
    }

    @Nested
    @DisplayName("createRoute Tests")
    class CreateRouteTests {

        @Test
        @DisplayName("Should create route successfully")
        void createRoute_success() {
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(routeRepository.save(any(Route.class))).thenReturn(route);
            when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

            RouteResponse result = routeService.createRoute(routeRequest);

            assertThat(result).isNotNull();
            assertThat(result.origin()).isEqualTo("ISTANBUL");
            assertThat(result.destination()).isEqualTo("ANKARA");
            assertThat(result.price()).isEqualTo(250.0);
            verify(routeRepository).save(any(Route.class));
        }

        @Test
        @DisplayName("Should throw exception when bus not found")
        void createRoute_busNotFound() {
            when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> routeService.createRoute(routeRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Bus not found");

            verify(routeRepository, never()).save(any(Route.class));
        }
    }

    @Nested
    @DisplayName("getAllRoutes Tests")
    class GetAllRoutesTests {

        @Test
        @DisplayName("Should return all routes")
        void getAllRoutes_success() {
            when(routeRepository.findAll()).thenReturn(List.of(route));
            when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

            List<RouteResponse> result = routeService.getAllRoutes();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).origin()).isEqualTo("ISTANBUL");
        }

        @Test
        @DisplayName("Should return empty list when no routes exist")
        void getAllRoutes_emptyList() {
            when(routeRepository.findAll()).thenReturn(List.of());

            List<RouteResponse> result = routeService.getAllRoutes();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchRoutes Tests")
    class SearchRoutesTests {

        @Test
        @DisplayName("Should search routes with all filters")
        void searchRoutes_allFilters() {
            RouteSearchRequest searchRequest = new RouteSearchRequest("Istanbul", "Ankara", departureDate);

            when(routeRepository.searchRoutesWithFilters("ISTANBUL", "ANKARA", departureDate))
                    .thenReturn(List.of(route));
            when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

            List<RouteResponse> result = routeService.searchRoutes(searchRequest);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should search routes with null filters")
        void searchRoutes_nullFilters() {
            RouteSearchRequest searchRequest = new RouteSearchRequest(null, null, null);

            when(routeRepository.searchRoutesWithFilters(null, null, null))
                    .thenReturn(List.of(route));
            when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(routeResponse);

            List<RouteResponse> result = routeService.searchRoutes(searchRequest);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should search routes with blank origin and destination")
        void searchRoutes_blankFilters() {
            RouteSearchRequest searchRequest = new RouteSearchRequest("  ", "  ", null);

            when(routeRepository.searchRoutesWithFilters(null, null, null))
                    .thenReturn(List.of());

            List<RouteResponse> result = routeService.searchRoutes(searchRequest);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateRoute Tests")
    class UpdateRouteTests {

        @Test
        @DisplayName("Should update route successfully")
        void updateRoute_success() {
            RouteRequest updateRequest = new RouteRequest("Izmir", "Bursa", departureDate, departureTime, 150.0, 1L);
            RouteResponse updatedResponse = new RouteResponse(1L, "IZMIR", "BURSA", departureDate, departureTime, 150.0, 1L);

            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(routeRepository.save(any(Route.class))).thenReturn(route);
            when(routeMapper.toRouteResponse(any(Route.class))).thenReturn(updatedResponse);

            RouteResponse result = routeService.updateRoute(1L, updateRequest);

            assertThat(result.origin()).isEqualTo("IZMIR");
            assertThat(result.destination()).isEqualTo("BURSA");
            assertThat(result.price()).isEqualTo(150.0);
        }

        @Test
        @DisplayName("Should throw exception when route not found for update")
        void updateRoute_notFound() {
            when(routeRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> routeService.updateRoute(999L, routeRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Route not found");
        }

        @Test
        @DisplayName("Should throw exception when bus not found for route update")
        void updateRoute_busNotFound() {
            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> routeService.updateRoute(1L, routeRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Bus not found");
        }
    }

    @Nested
    @DisplayName("deleteRoute Tests")
    class DeleteRouteTests {

        @Test
        @DisplayName("Should delete route successfully")
        void deleteRoute_success() {
            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(ticketRepository.existsByRouteId(1L)).thenReturn(false);

            routeService.deleteRoute(1L);

            verify(routeRepository).delete(route);
        }

        @Test
        @DisplayName("Should throw exception when route has associated tickets")
        void deleteRoute_hasTickets() {
            when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
            when(ticketRepository.existsByRouteId(1L)).thenReturn(true);

            assertThatThrownBy(() -> routeService.deleteRoute(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("associated tickets");

            verify(routeRepository, never()).delete(any(Route.class));
        }

        @Test
        @DisplayName("Should throw exception when route not found for delete")
        void deleteRoute_notFound() {
            when(routeRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> routeService.deleteRoute(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
