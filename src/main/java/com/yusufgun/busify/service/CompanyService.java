package com.yusufgun.busify.service;

import com.yusufgun.busify.dto.CompanyRequest;
import com.yusufgun.busify.dto.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import com.yusufgun.busify.exception.ResourceAlreadyExistsException;
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
        if (companyRepository.existsByName(companyRequest.getName())) {
            throw new ResourceAlreadyExistsException("Company with name '" + companyRequest.getName() + "' already exists");
        }

        Company company = new Company();
        company.setName(companyRequest.getName());
        company.setContactNumber(companyRequest.getContactNumber());

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toCompanyResponse(savedCompany);
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toCompanyResponse)
                .collect(Collectors.toList());
    }


}
