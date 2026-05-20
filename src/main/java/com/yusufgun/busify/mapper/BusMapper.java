package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.BusResponse;
import com.yusufgun.busify.entity.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(source = "company.id", target = "companyId")
    BusResponse toBusResponse(Bus bus);
}