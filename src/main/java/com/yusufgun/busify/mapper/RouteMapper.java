package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.entity.Route;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    RouteResponse toRouteResponse(Route route);
}