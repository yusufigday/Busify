package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.entity.Route;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(source = "bus.id", target = "busId")
    RouteResponse toRouteResponse(Route route);
}