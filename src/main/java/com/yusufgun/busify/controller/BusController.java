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

    @PostMapping("/create")
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody BusRequest busRequest){
        return ResponseEntity.ok(busService.createBus(busRequest));
    }

    @GetMapping("/allBuses")
    public ResponseEntity<List<BusResponse>> allBuses(){
        return ResponseEntity.ok(busService.getAllBuses());
    }


}
