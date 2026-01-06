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


import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quiz_survey")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class QuizSurveyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type;
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private SurveyDefinition definitionJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> answerKey;

    private Integer maxScore;
    private Boolean status;

    private String quizTotalDuration;
    private String quizDuration;

    private Boolean isAnnounced;
    private Boolean isMandatory;

    private Integer maxRetake;

    @ElementCollection
    @CollectionTable(
            name = "quiz_targeted_users",
            joinColumns = @JoinColumn(name = "quiz_id")
    )
    @Column(name = "user_id")
    private Set<String> targetedUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "quiz_user_data_fields",
            joinColumns = @JoinColumn(name = "quiz_id")
    )
    @Column(name = "field_name")
    private Set<String> userDataDisplayFields = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private VisibilityType visibilityType;

    @Enumerated(EnumType.STRING)
    private AnnouncementMode announcementMode;

    private Instant scheduledTime;

    @CreatedDate
    private Instant createdAt;
}
