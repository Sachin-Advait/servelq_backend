package com.gis.servelq.dto;


import com.gis.servelq.models.AnnouncementMode;
import com.gis.servelq.models.SurveyDefinition;
import com.gis.servelq.models.VisibilityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSurveyDTO {
  private UUID id;
  private String type;
  private String title;
  private SurveyDefinition definitionJson;
  private  Map<String, Object> answerKey;
  private Integer maxScore;
  private String quizDuration;
  private String quizTotalDuration;
  private Instant createdAt;
  private Integer maxRetake;
  private VisibilityType visibilityType;
  private AnnouncementMode announcementMode;
  private Set<String> userDataDisplayFields;
}
