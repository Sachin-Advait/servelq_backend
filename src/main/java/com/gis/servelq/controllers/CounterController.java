package com.gis.servelq.controllers;

import com.gis.servelq.dto.CounterDTO;
import com.gis.servelq.services.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class CounterController {
    private final CounterService counterService;

    // Get all branches
    @GetMapping
    public ResponseEntity<List<CounterDTO>> getAllCounters() {
        List<CounterDTO> counters = counterService.getAllCounters();
        return ResponseEntity.ok(counters);
    }

    @GetMapping("/{counterId}")
    public ResponseEntity<CounterDTO> getCounterById(@PathVariable String counterId) {
        CounterDTO counter = counterService.getCounterById(counterId);
        return ResponseEntity.ok(counter);
    }
}
