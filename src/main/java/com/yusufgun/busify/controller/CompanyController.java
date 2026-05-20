package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.CompanyRequest;
import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest companyRequest){
        return ResponseEntity.ok(companyService.createCompany(companyRequest));
    }

    @GetMapping("/allCompanies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

}
