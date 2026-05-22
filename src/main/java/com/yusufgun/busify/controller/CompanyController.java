package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.CompanyRequest;
import com.yusufgun.busify.dto.response.CompanyResponse;
import com.yusufgun.busify.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/allCompanies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long companyId){
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }

    @PostMapping("/create")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest companyRequest){
        return ResponseEntity.ok(companyService.createCompany(companyRequest));
    }

    @PutMapping("/update/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long companyId,@Valid @RequestBody CompanyRequest updatedRequest){
        return ResponseEntity.ok(companyService.updateCompany(companyId, updatedRequest));
    }

    @DeleteMapping("/delete/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId){
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }



}
