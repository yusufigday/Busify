package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE " +
            "(:origin IS NULL OR r.origin = :origin) AND " +
            "(:destination IS NULL OR r.destination = :destination) AND " +
            "(CAST(:date AS date) IS NULL OR r.departureDate = :date)")
    List<Route> searchRoutesWithFilters(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );

    boolean existsByBusId(Long busId);

}