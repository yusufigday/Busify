package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByOriginAndDestinationAndDepartureDate(String origin, String destination, LocalDate date);

}
