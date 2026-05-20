package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.TicketResponse;
import com.yusufgun.busify.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "user.tcNo", target = "userTcNo")
    @Mapping(source = "route.price", target = "price")
    TicketResponse toTicketResponse(Ticket ticket);
}