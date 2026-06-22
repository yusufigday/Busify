package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.BusRequest;
import com.yusufgun.busify.dto.response.BusResponse;
import com.yusufgun.busify.entity.Bus;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.BusMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.CompanyRepository;
import com.yusufgun.busify.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final CompanyRepository companyRepository;
    private final RouteRepository routeRepository;
    private final BusMapper busMapper;

    public BusResponse createBus(BusRequest busRequest) {
        String cleanPlate = busRequest.plate().trim().toUpperCase();

        if (busRepository.existsByPlate(cleanPlate)) {
            throw new ResourceAlreadyExistsException("Bus with plate '" + cleanPlate + "' already exists");
        }

        Company company = companyRepository.findById(busRequest.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + busRequest.companyId()));

        Bus bus = new Bus();
        bus.setPlate(cleanPlate);
        bus.setCapacity(busRequest.capacity());
        bus.setCompany(company);

        Bus savedBus = busRepository.save(bus);
        return busMapper.toBusResponse(savedBus);
    }

    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::toBusResponse)
                .collect(Collectors.toList());
    }

    public BusResponse getBus(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus with id: " + busId + " not found"));

        return busMapper.toBusResponse(bus);
    }

    public BusResponse updateBus(Long busId, BusRequest updatedRequest) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus with id: " + busId + " not found"));

        String requestedPlate = updatedRequest.plate().trim().toUpperCase();
        String currentPlate = bus.getPlate().trim().toUpperCase();

        if (!currentPlate.equals(requestedPlate) && busRepository.existsByPlate(requestedPlate)) {
            throw new ResourceAlreadyExistsException("Bus with plate '" + requestedPlate + "' already exists");
        }

        Company company = companyRepository.findById(updatedRequest.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + updatedRequest.companyId()));

        bus.setPlate(requestedPlate);
        bus.setCapacity(updatedRequest.capacity());
        bus.setCompany(company);

        return busMapper.toBusResponse(busRepository.save(bus));
    }

    public void deleteBus(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus with id: " + busId + " not found"));

        if (routeRepository.existsByBusId(busId)) {
            throw new IllegalStateException("Cannot delete bus with id '" + busId + "' because it has associated routes");
        }

        busRepository.delete(bus);
    }
}