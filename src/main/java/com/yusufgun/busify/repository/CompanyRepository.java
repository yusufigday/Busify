package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByName(String name);

    boolean existsByContactNumber(String contactNumber);
}
