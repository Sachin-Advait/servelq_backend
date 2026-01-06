package com.gis.servelq.repository;


import com.gis.servelq.models.ResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResponseRepo
        extends JpaRepository<ResponseModel, UUID> {

    List<ResponseModel> findByQuizSurveyId(UUID quizSurveyId);

    List<ResponseModel> findByUserId(String userId);

    List<ResponseModel> findByQuizSurveyIdAndUserId(
            UUID quizSurveyId,
            String userId
    );

    // 🔥 Optimized participation check
    @Query("""
        SELECT COUNT(r) > 0
        FROM ResponseModel r
        WHERE r.quizSurveyId = :quizId
          AND r.userId = :userId
    """)
    boolean hasUserParticipated(
            @Param("quizId") UUID quizId,
            @Param("userId") String userId
    );
}

