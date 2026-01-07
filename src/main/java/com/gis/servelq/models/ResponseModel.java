package com.gis.servelq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "responses",
        indexes = {
                @Index(columnList = "quiz_survey_id"),
                @Index(columnList = "user_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ResponseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quiz_survey_id", nullable = false)
    private UUID quizSurveyId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String username;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> answers;

    private Integer score;
    private Integer maxScore;

    @Column(name = "finish_time_ms")
    private Long finishTimeMs;

    @CreatedDate
    private Instant submittedAt;
}
