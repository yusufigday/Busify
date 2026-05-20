package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.entity.Route;
import org.springframework.stereotype.Component;

@Component
public class RouteMapper {

    public RouteResponse toRouteResponse(Route route) {
        if (route == null) {
            return null;
        }

        RouteResponse response = new RouteResponse();
        response.setId(route.getId());
        response.setOrigin(route.getOrigin());
        response.setDestination(route.getDestination());
        response.setDepartureDate(route.getDepartureDate());
        response.setDepartureTime(route.getDepartureTime());
        response.setPrice(route.getPrice());
        return response;
    }
}
