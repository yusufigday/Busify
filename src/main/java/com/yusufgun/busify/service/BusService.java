package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.BusRequest;
import com.yusufgun.busify.dto.BusResponse;
import com.yusufgun.busify.entity.Bus;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.BusMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final CompanyRepository companyRepository;
    private final BusMapper busMapper;

    public BusResponse createBus(BusRequest busRequest){
        if (busRepository.existsByPlate(busRequest.getPlate())){
            throw new ResourceAlreadyExistsException("Bus with plate '" + busRequest.getPlate() + "' already exists");
        }

        Company company = companyRepository.findById(busRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + busRequest.getCompanyId()));

        Bus bus = new Bus();
        bus.setPlate(busRequest.getPlate());
        bus.setCapacity(busRequest.getCapacity());
        bus.setCompany(company);

        Bus savedBus = busRepository.save(bus);
        return busMapper.toBusResponse(savedBus);
    }

    public List<BusResponse> getAllBuses(){
        return busRepository.findAll().stream()
                .map(busMapper::toBusResponse)
                .collect(Collectors.toList());
    }

}
