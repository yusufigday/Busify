package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.CompanyRequest;
import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.CompanyMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.CompanyRepository;
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
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private CompanyService companyService;

    private Company company;
    private CompanyRequest companyRequest;
    private CompanyResponse companyResponse;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("METRO TURIZM");
        company.setContactNumber("05001112233");

        companyRequest = new CompanyRequest("Metro Turizm", "05001112233");
        companyResponse = new CompanyResponse(1L, "METRO TURIZM", "05001112233");
    }

    @Nested
    @DisplayName("createCompany Tests")
    class CreateCompanyTests {

        @Test
        @DisplayName("Should create company successfully")
        void createCompany_success() {
            when(companyRepository.existsByName(anyString())).thenReturn(false);
            when(companyRepository.save(any(Company.class))).thenReturn(company);
            when(companyMapper.toCompanyResponse(any(Company.class))).thenReturn(companyResponse);

            CompanyResponse result = companyService.createCompany(companyRequest);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("METRO TURIZM");
            assertThat(result.contactNumber()).isEqualTo("05001112233");
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("Should throw exception when company name already exists")
        void createCompany_nameAlreadyExists() {
            when(companyRepository.existsByName(anyString())).thenReturn(true);

            assertThatThrownBy(() -> companyService.createCompany(companyRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("already exists");

            verify(companyRepository, never()).save(any(Company.class));
        }
    }

    @Nested
    @DisplayName("getAllCompanies Tests")
    class GetAllCompaniesTests {

        @Test
        @DisplayName("Should return all companies")
        void getAllCompanies_success() {
            when(companyRepository.findAll()).thenReturn(List.of(company));
            when(companyMapper.toCompanyResponse(any(Company.class))).thenReturn(companyResponse);

            List<CompanyResponse> result = companyService.getAllCompanies();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("METRO TURIZM");
        }

        @Test
        @DisplayName("Should return empty list when no companies exist")
        void getAllCompanies_emptyList() {
            when(companyRepository.findAll()).thenReturn(List.of());

            List<CompanyResponse> result = companyService.getAllCompanies();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCompanyById Tests")
    class GetCompanyByIdTests {

        @Test
        @DisplayName("Should return company by id")
        void getCompanyById_success() {
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(companyMapper.toCompanyResponse(company)).thenReturn(companyResponse);

            CompanyResponse result = companyService.getCompanyById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when company not found")
        void getCompanyById_notFound() {
            when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.getCompanyById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("updateCompany Tests")
    class UpdateCompanyTests {

        @Test
        @DisplayName("Should update company successfully")
        void updateCompany_success() {
            CompanyRequest updateRequest = new CompanyRequest("Kamil Koc", "05009998877");
            CompanyResponse updatedResponse = new CompanyResponse(1L, "KAMIL KOC", "05009998877");

            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(companyRepository.existsByName(anyString())).thenReturn(false);
            when(companyRepository.existsByContactNumber(anyString())).thenReturn(false);
            when(companyRepository.save(any(Company.class))).thenReturn(company);
            when(companyMapper.toCompanyResponse(any(Company.class))).thenReturn(updatedResponse);

            CompanyResponse result = companyService.updateCompany(1L, updateRequest);

            assertThat(result.name()).isEqualTo("KAMIL KOC");
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("Should update company when name unchanged")
        void updateCompany_sameName() {
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(companyRepository.save(any(Company.class))).thenReturn(company);
            when(companyMapper.toCompanyResponse(any(Company.class))).thenReturn(companyResponse);

            CompanyResponse result = companyService.updateCompany(1L, companyRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when new name already exists")
        void updateCompany_nameAlreadyExists() {
            CompanyRequest updateRequest = new CompanyRequest("Kamil Koc", "05009998877");

            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(companyRepository.existsByName(anyString())).thenReturn(true);

            assertThatThrownBy(() -> companyService.updateCompany(1L, updateRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);
        }

        @Test
        @DisplayName("Should throw exception when new contact number already exists")
        void updateCompany_contactAlreadyExists() {
            CompanyRequest updateRequest = new CompanyRequest("Metro Turizm", "05559998877");

            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(companyRepository.existsByContactNumber("05559998877")).thenReturn(true);

            assertThatThrownBy(() -> companyService.updateCompany(1L, updateRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);
        }

        @Test
        @DisplayName("Should throw exception when company not found for update")
        void updateCompany_notFound() {
            when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.updateCompany(999L, companyRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteCompany Tests")
    class DeleteCompanyTests {

        @Test
        @DisplayName("Should delete company successfully")
        void deleteCompany_success() {
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(busRepository.existsByCompanyId(1L)).thenReturn(false);

            companyService.deleteCompany(1L);

            verify(companyRepository).delete(company);
        }

        @Test
        @DisplayName("Should throw exception when company has associated buses")
        void deleteCompany_hasBuses() {
            when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
            when(busRepository.existsByCompanyId(1L)).thenReturn(true);

            assertThatThrownBy(() -> companyService.deleteCompany(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("associated buses");

            verify(companyRepository, never()).delete(any(Company.class));
        }

        @Test
        @DisplayName("Should throw exception when company not found for delete")
        void deleteCompany_notFound() {
            when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.deleteCompany(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
