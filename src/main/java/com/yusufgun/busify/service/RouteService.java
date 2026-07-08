package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.RouteRequest;
import com.yusufgun.busify.dto.request.RouteSearchRequest;
import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.entity.Bus;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.RouteMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.RouteRepository;
import com.yusufgun.busify.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final BusRepository busRepository;
    private final TicketRepository ticketRepository;

    public List<RouteResponse> searchRoutes(RouteSearchRequest searchRequest) {
        String cleanOrigin = (searchRequest.origin() != null && !searchRequest.origin().isBlank())
                ? searchRequest.origin().trim().toUpperCase()
                : null;

        String cleanDestination = (searchRequest.destination() != null && !searchRequest.destination().isBlank())
                ? searchRequest.destination().trim().toUpperCase()
                : null;

        return routeRepository.searchRoutesWithFilters(
                        cleanOrigin,
                        cleanDestination,
                        searchRequest.date()
                ).stream()
                .map(routeMapper::toRouteResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "routes", unless = "#result.isEmpty()")
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toRouteResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "routes", allEntries = true)
    public RouteResponse createRoute(RouteRequest request) {
        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + request.busId()));

        Route route = new Route();
        route.setOrigin(request.origin().trim().toUpperCase());
        route.setDestination(request.destination().trim().toUpperCase());
        route.setDepartureDate(request.departureDate());
        route.setDepartureTime(request.departureTime());
        route.setPrice(request.price());
        route.setBus(bus);

        Route savedRoute = routeRepository.save(route);
        return routeMapper.toRouteResponse(savedRoute);
    }

    @CacheEvict(value = "routes", allEntries = true)
    public RouteResponse updateRoute(Long routeId, RouteRequest updatedRoute) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        Bus bus = busRepository.findById(updatedRoute.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + updatedRoute.busId()));

        route.setOrigin(updatedRoute.origin().trim().toUpperCase());
        route.setDestination(updatedRoute.destination().trim().toUpperCase());
        route.setDepartureDate(updatedRoute.departureDate());
        route.setDepartureTime(updatedRoute.departureTime());
        route.setPrice(updatedRoute.price());
        route.setBus(bus);

        return routeMapper.toRouteResponse(routeRepository.save(route));
    }

    @CacheEvict(value = "routes", allEntries = true)
    public void deleteRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        if (ticketRepository.existsByRouteId(routeId)) {
            throw new IllegalStateException("Cannot delete route with id '" + routeId + "' because it has associated tickets");
        }

        routeRepository.delete(route);
    }
}