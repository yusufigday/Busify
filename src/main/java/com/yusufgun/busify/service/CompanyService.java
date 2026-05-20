package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.request.CompanyRequest;
import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
import com.yusufgun.busify.exception.ResourceNotFoundException;
import com.yusufgun.busify.mapper.CompanyMapper;
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

    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        if (companyRepository.existsByName(companyRequest.name())) {
            throw new ResourceAlreadyExistsException("Company with name '" + companyRequest.name() + "' already exists");
        }

        Company company = new Company();
        company.setName(companyRequest.name());
        company.setContactNumber(companyRequest.contactNumber());

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

        if (!company.getName().equals(updatedRequest.name()) && companyRepository.existsByName(updatedRequest.name())) {
            throw new ResourceAlreadyExistsException("Company with name '" + updatedRequest.name() + "' already exists");
        }

        company.setName(updatedRequest.name());
        company.setContactNumber(updatedRequest.contactNumber());

        return  companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company with id '" + companyId + "' not found"));

        companyRepository.delete(company);
    }
}