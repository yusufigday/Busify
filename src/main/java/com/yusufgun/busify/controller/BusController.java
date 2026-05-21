package com.yusufgun.busify.controller;

import com.yusufgun.busify.dto.request.BusRequest;
import com.yusufgun.busify.dto.response.BusResponse;
import com.yusufgun.busify.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @GetMapping("/allBuses")
    public ResponseEntity<List<BusResponse>> allBuses(){
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @GetMapping("/{busId}")
    public ResponseEntity<BusResponse> getBusById(@PathVariable Long busId){
        return ResponseEntity.ok(busService.getBus(busId));
    }

    @PostMapping("/create")
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody BusRequest busRequest){
        return ResponseEntity.ok(busService.createBus(busRequest));
    }

    @PutMapping("/update/{busId}")
    public ResponseEntity<BusResponse> updateBus(@PathVariable Long busId, @Valid @RequestBody BusRequest updatedRequest){
        return ResponseEntity.ok(busService.updateBus(busId, updatedRequest));
    }

    @DeleteMapping("/delete/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId){
        busService.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }
}