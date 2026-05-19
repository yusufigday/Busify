package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.BusResponse;
import com.yusufgun.busify.entity.Bus;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {

    public BusResponse toBusResponse(Bus bus){
        if (bus == null){
            return null;
        }
        BusResponse busResponse = new BusResponse();
        busResponse.setId(bus.getId());
        busResponse.setPlate(bus.getPlate());
        busResponse.setCapacity(bus.getCapacity());

        if (bus.getCompany() != null){
            busResponse.setCompanyName(bus.getCompany().getName());
        }

        return busResponse;
    }
}
