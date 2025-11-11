package com.gis.servelq.services;

import com.gis.servelq.dto.CounterDTO;
import com.gis.servelq.models.Counter;
import com.gis.servelq.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounterService {
    private final CounterRepository counterRepository;

    public List<CounterDTO> getAllCounters() {
        return counterRepository.findAll()
                .stream()
                .map(CounterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CounterDTO getCounterById(String counterId) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("counter not found"));
        return CounterDTO.fromEntity(counter);
    }
}
