package com.gis.servelq.controllers;

import com.gis.servelq.models.Visitor;
import com.gis.servelq.services.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serveiq/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    // CREATE
    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@RequestBody Visitor visitor) {
        return ResponseEntity.ok(visitorService.createVisitor(visitor));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        return ResponseEntity.ok(visitorService.getAllVisitors());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitorById(@PathVariable String id) {
        return visitorService.getVisitorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
