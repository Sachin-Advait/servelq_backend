package com.gis.servelq.dto;

import com.gis.servelq.models.VisibilityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizzesSurveysDTO {
    private UUID id;
    private String type;
    private String title;
    private Integer totalQuestion;
    private Boolean status;
    private String quizTotalDuration;
    private String quizDuration;
    private Boolean isAnnounced;
    private Boolean isParticipated;
    private Boolean isMandatory;
    private Instant createdAt;
    private Integer maxRetake;
    private Set<String> userDataDisplayFields;
    private VisibilityType visibilityType;
}
