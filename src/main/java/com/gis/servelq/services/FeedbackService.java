package com.gis.servelq.services;

import com.gis.servelq.models.Counter;
import com.gis.servelq.models.CounterStatus;
import com.gis.servelq.models.Feedback;
import com.gis.servelq.repository.CounterRepository;
import com.gis.servelq.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CounterRepository counterRepository;
    private final AgentService agentService;

    public Feedback createFeedback(Feedback feedback) {

        Counter counter = counterRepository.findByCode(feedback.getCounterCode())
                .orElseThrow(() -> new RuntimeException("Counter not found"));
        counter.setStatus(CounterStatus.IDLE);
        counterRepository.save(counter);

        agentService.notifyAgentUpcoming(counter.getId());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public void delete(String id) {
        feedbackRepository.deleteById(id);
    }

    public Map<String, Long> getFeedbackSummary() {
        List<Feedback> all = feedbackRepository.findAll();

        long happy = all.stream()
                .filter(f -> f.getRating() == Feedback.MoodRating.HAPPY)
                .count();

        long neutral = all.stream()
                .filter(f -> f.getRating() == Feedback.MoodRating.NEUTRAL)
                .count();

        long sad = all.stream()
                .filter(f -> f.getRating() == Feedback.MoodRating.SAD)
                .count();

        Map<String, Long> summary = new HashMap<>();
        summary.put("totalHappy", happy);
        summary.put("totalNeutral", neutral);
        summary.put("totalSad", sad);

        return summary;
    }
}
