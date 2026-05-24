package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByPlate(String plate);

    boolean existsByCompanyId(Long companyId);

}
