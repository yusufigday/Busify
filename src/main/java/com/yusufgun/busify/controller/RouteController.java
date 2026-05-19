package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.RouteRequest;
import com.yusufgun.busify.dto.RouteResponse;
import com.yusufgun.busify.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/allRoutes")
    public ResponseEntity<List<RouteResponse>> getAllRoutes(){
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteResponse>> searchRoutes(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(routeService.searchRoutes(origin, destination, date));
    }

    @PostMapping("/add")
    public ResponseEntity<RouteResponse> addRoute(@RequestBody RouteRequest request){
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    @PutMapping("/update/{routeId}")
    public ResponseEntity<RouteResponse> updateRoute(@PathVariable Long routeId, @RequestBody RouteRequest updatedRoute){
        return ResponseEntity.ok(routeService.updateRoute(routeId,updatedRoute));
    }

    @DeleteMapping("/delete/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId){
        routeService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }


}
