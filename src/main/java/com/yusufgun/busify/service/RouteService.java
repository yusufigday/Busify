package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.RouteRequest;
import com.yusufgun.busify.dto.RouteResponse;
import com.yusufgun.busify.entity.Bus;
import com.yusufgun.busify.entity.Route;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.RouteMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final BusRepository busRepository;

    public List<RouteResponse> searchRoutes(String origin, String destination, LocalDate date) {

        List<Route> routes = routeRepository.findByOriginAndDestinationAndDepartureDate(origin, destination, date);

        return routes.stream().map(routeMapper::toRouteResponse).collect(Collectors.toList());

    }

    public List<RouteResponse> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();

        return routes.stream()
                .map(routeMapper::toRouteResponse)
                .collect(Collectors.toList());

    }

    public RouteResponse createRoute(RouteRequest request) {

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + request.getBusId()));

        Route route = new Route();
        route.setOrigin(request.getOrigin());
        route.setDestination(request.getDestination());
        route.setDepartureDate(request.getDepartureDate());
        route.setDepartureTime(request.getDepartureTime());
        route.setPrice(request.getPrice());

        route.setBus(bus);

        Route savedRoute = routeRepository.save(route);

        return routeMapper.toRouteResponse(savedRoute);
    }


    public RouteResponse updateRoute(Long routeId, RouteRequest updatedRoute) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        route.setOrigin(updatedRoute.getOrigin());
        route.setDestination(updatedRoute.getDestination());
        route.setDepartureDate(updatedRoute.getDepartureDate());
        route.setDepartureTime(updatedRoute.getDepartureTime());
        route.setPrice(updatedRoute.getPrice());

        return routeMapper.toRouteResponse(routeRepository.save(route));
    }

    public void deleteRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        routeRepository.delete(route);
    }
}
