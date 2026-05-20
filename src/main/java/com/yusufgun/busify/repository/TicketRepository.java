package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByRouteId(Long routeId);

    boolean existsByRouteIdAndSeatNumber(Long routeId, int seatNumber);
}
