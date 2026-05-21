package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.CompanyRequest;
import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.CompanyMapper;
import com.yusufgun.busify.repository.BusRepository;
import com.yusufgun.busify.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final BusRepository busRepository;

    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        String cleanName = companyRequest.name().trim().toUpperCase();
        String cleanContact = companyRequest.contactNumber().trim();

        if (companyRepository.existsByName(cleanName)) {
            throw new ResourceAlreadyExistsException("Company with name '" + cleanName + "' already exists");
        }

        Company company = new Company();
        company.setName(cleanName);
        company.setContactNumber(cleanContact);

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toCompanyResponse(savedCompany);
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toCompanyResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company with id '" + companyId + "' not found"));

        return companyMapper.toCompanyResponse(company);
    }

    public CompanyResponse updateCompany(Long companyId, CompanyRequest updatedRequest) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company with id '" + companyId + "' not found"));

        String requestedName = updatedRequest.name().trim().toUpperCase();
        String currentName = company.getName().trim().toUpperCase();
        String cleanContact = updatedRequest.contactNumber().trim();

        if (!currentName.equals(requestedName) && companyRepository.existsByName(requestedName)) {
            throw new ResourceAlreadyExistsException("Company with name '" + requestedName + "' already exists");
        }

        company.setName(requestedName);
        company.setContactNumber(cleanContact);

        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company with id '" + companyId + "' not found"));

        if (busRepository.existsByCompanyId(companyId)) {
            throw new IllegalStateException("Cannot delete company with id '" + companyId + "' because it has associated buses");
        }

        companyRepository.delete(company);
    }
}