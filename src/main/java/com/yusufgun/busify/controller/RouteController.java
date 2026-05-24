package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.RouteRequest;
import com.yusufgun.busify.dto.request.RouteSearchRequest;
import com.yusufgun.busify.dto.response.RouteResponse;
import com.yusufgun.busify.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<RouteResponse>> searchRoutes(@ModelAttribute RouteSearchRequest searchRequest){
        return ResponseEntity.ok(routeService.searchRoutes(searchRequest));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<RouteResponse> addRoute(@RequestBody RouteRequest request){
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    @PutMapping("/update/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<RouteResponse> updateRoute(@PathVariable Long routeId, @RequestBody RouteRequest updatedRoute){
        return ResponseEntity.ok(routeService.updateRoute(routeId,updatedRoute));
    }

    @DeleteMapping("/delete/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId){
        routeService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }

}