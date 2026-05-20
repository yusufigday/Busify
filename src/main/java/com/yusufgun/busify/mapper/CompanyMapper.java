package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toCompanyResponse(Company company){
        if (company == null) {
            return null;
        }

        CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setId(company.getId());
        companyResponse.setName(company.getName());
        companyResponse.setContactNumber(company.getContactNumber());
        return companyResponse;
    }
}
