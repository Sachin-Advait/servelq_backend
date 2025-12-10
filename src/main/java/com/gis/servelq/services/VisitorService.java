package com.gis.servelq.services;

import com.gis.servelq.models.Visitor;
import com.gis.servelq.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public Optional<Visitor> getVisitorById(String visitorId) {
        return visitorRepository.findByVisitorId(visitorId);
    }

    public Visitor createVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }
}
