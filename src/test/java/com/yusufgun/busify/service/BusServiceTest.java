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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private BusMapper busMapper;

    @InjectMocks
    private BusService busService;

    private Company company;
    private Bus bus;
    private BusRequest busRequest;
    private BusResponse busResponse;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("TEST COMPANY");
        company.setContactNumber("05001234567");

        bus = new Bus();
        bus.setId(1L);
        bus.setPlate("34 ABC 123");
        bus.setCapacity(45);
        bus.setCompany(company);

        busRequest = new BusRequest("34 ABC 123", 45, 1L);
        busResponse = new BusResponse(1L, "34 ABC 123", 45, 1L);
    }

    @Nested
    @DisplayName("createBus Tests")
    class CreateBusTests {

        @Test
        @DisplayName("Should create bus successfully")
        void createBus_success() {
            when(busRepository.existsByPlate(anyString())).thenReturn(false);
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(busRepository.save(any(Bus.class))).thenReturn(bus);
            when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

            BusResponse result = busService.createBus(busRequest);

            assertThat(result).isNotNull();
            assertThat(result.plate()).isEqualTo("34 ABC 123");
            assertThat(result.capacity()).isEqualTo(45);
            assertThat(result.companyId()).isEqualTo(1L);
            verify(busRepository).save(any(Bus.class));
        }

        @Test
        @DisplayName("Should throw exception when plate already exists")
        void createBus_plateAlreadyExists() {
            when(busRepository.existsByPlate(anyString())).thenReturn(true);

            assertThatThrownBy(() -> busService.createBus(busRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("already exists");

            verify(busRepository, never()).save(any(Bus.class));
        }

        @Test
        @DisplayName("Should throw exception when company not found")
        void createBus_companyNotFound() {
            when(busRepository.existsByPlate(anyString())).thenReturn(false);
            when(companyRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> busService.createBus(busRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Company not found");

            verify(busRepository, never()).save(any(Bus.class));
        }
    }

    @Nested
    @DisplayName("getAllBuses Tests")
    class GetAllBusesTests {

        @Test
        @DisplayName("Should return all buses")
        void getAllBuses_success() {
            when(busRepository.findAll()).thenReturn(List.of(bus));
            when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

            List<BusResponse> result = busService.getAllBuses();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).plate()).isEqualTo("34 ABC 123");
        }

        @Test
        @DisplayName("Should return empty list when no buses exist")
        void getAllBuses_emptyList() {
            when(busRepository.findAll()).thenReturn(List.of());

            List<BusResponse> result = busService.getAllBuses();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getBus Tests")
    class GetBusTests {

        @Test
        @DisplayName("Should return bus by id")
        void getBus_success() {
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(busMapper.toBusResponse(bus)).thenReturn(busResponse);

            BusResponse result = busService.getBus(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when bus not found")
        void getBus_notFound() {
            when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> busService.getBus(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("updateBus Tests")
    class UpdateBusTests {

        @Test
        @DisplayName("Should update bus successfully")
        void updateBus_success() {
            BusRequest updateRequest = new BusRequest("34 XYZ 789", 50, 1L);
            BusResponse updatedResponse = new BusResponse(1L, "34 XYZ 789", 50, 1L);

            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(busRepository.existsByPlate("34 XYZ 789")).thenReturn(false);
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(busRepository.save(any(Bus.class))).thenReturn(bus);
            when(busMapper.toBusResponse(any(Bus.class))).thenReturn(updatedResponse);

            BusResponse result = busService.updateBus(1L, updateRequest);

            assertThat(result.plate()).isEqualTo("34 XYZ 789");
            assertThat(result.capacity()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should update bus when plate unchanged")
        void updateBus_samePlate() {
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(busRepository.save(any(Bus.class))).thenReturn(bus);
            when(busMapper.toBusResponse(any(Bus.class))).thenReturn(busResponse);

            BusResponse result = busService.updateBus(1L, busRequest);

            assertThat(result).isNotNull();
            verify(busRepository, never()).existsByPlate(anyString());
        }

        @Test
        @DisplayName("Should throw exception when new plate already exists")
        void updateBus_plateAlreadyExists() {
            BusRequest updateRequest = new BusRequest("34 XYZ 789", 50, 1L);

            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(busRepository.existsByPlate("34 XYZ 789")).thenReturn(true);

            assertThatThrownBy(() -> busService.updateBus(1L, updateRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);
        }

        @Test
        @DisplayName("Should throw exception when bus not found for update")
        void updateBus_busNotFound() {
            when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> busService.updateBus(999L, busRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteBus Tests")
    class DeleteBusTests {

        @Test
        @DisplayName("Should delete bus successfully")
        void deleteBus_success() {
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(routeRepository.existsByBusId(1L)).thenReturn(false);

            busService.deleteBus(1L);

            verify(busRepository).delete(bus);
        }

        @Test
        @DisplayName("Should throw exception when bus has associated routes")
        void deleteBus_hasRoutes() {
            when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
            when(routeRepository.existsByBusId(1L)).thenReturn(true);

            assertThatThrownBy(() -> busService.deleteBus(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("associated routes");

            verify(busRepository, never()).delete(any(Bus.class));
        }

        @Test
        @DisplayName("Should throw exception when bus not found for delete")
        void deleteBus_notFound() {
            when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> busService.deleteBus(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
