package com.gis.servelq.services;

import com.gis.servelq.models.Feedback;
import com.gis.servelq.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repo;

    public Feedback createFeedback(Feedback f) {
        return repo.save(f);
    }

    public List<Feedback> getAll() {
        return repo.findAll();
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
