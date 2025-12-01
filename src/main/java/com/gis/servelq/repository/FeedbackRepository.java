package com.gis.servelq.repository;

import com.gis.servelq.models.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {
}
