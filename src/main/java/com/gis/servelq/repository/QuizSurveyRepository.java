package com.gis.servelq.repository;

import com.gis.servelq.models.AnnouncementMode;
import com.gis.servelq.models.QuizSurveyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuizSurveyRepository
        extends JpaRepository<QuizSurveyModel, UUID>,
        JpaSpecificationExecutor<QuizSurveyModel> {

    // 🔔 Scheduled announcements (cron job use-case)
    @Query("""
        SELECT q
        FROM QuizSurveyModel q
        WHERE q.announcementMode = :mode
          AND q.scheduledTime < :time
          AND q.isAnnounced = false
    """)
    List<QuizSurveyModel> findPendingScheduledAnnouncements(
            @Param("mode") AnnouncementMode mode,
            @Param("time") Instant time
    );

    // 📌 Get all quizzes by type (case-insensitive)
    @Query("""
        SELECT q
        FROM QuizSurveyModel q
        WHERE LOWER(q.type) = LOWER(:type)
    """)
    List<QuizSurveyModel> findByTypeIgnoreCase(@Param("type") String type);

    // 👤 Quizzes targeted to a user
    @Query("""
        SELECT DISTINCT q
        FROM QuizSurveyModel q
        JOIN q.targetedUsers tu
        WHERE tu = :userId
    """)
    List<QuizSurveyModel> findByTargetedUser(@Param("userId") String userId);

    // 👤 Quizzes by type + targeted user
    @Query("""
        SELECT DISTINCT q
        FROM QuizSurveyModel q
        JOIN q.targetedUsers tu
        WHERE LOWER(q.type) = LOWER(:type)
          AND tu = :userId
    """)
    List<QuizSurveyModel> findByTypeAndTargetedUser(
            @Param("type") String type,
            @Param("userId") String userId
    );
}
