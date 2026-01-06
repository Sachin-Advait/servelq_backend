package com.gis.servelq.repository;


import com.gis.servelq.models.QuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository
        extends JpaRepository<QuestionModel, UUID> {
}
