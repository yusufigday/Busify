package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toCompanyResponse(Company company);
}