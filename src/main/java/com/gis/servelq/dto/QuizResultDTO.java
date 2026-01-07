package com.gis.servelq.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private UUID id;
    private String username;
    private Integer score;
    private Integer maxScore;
    private Instant submittedAt;
    private Long finishTime;
    private Map<String, QuestionAnswerDTO> answers;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static public class QuestionAnswerDTO {
        private List<OptionDTO> choices;
        private String type;
        private String arabicTitle;
        private Object selectedOptions;
        private String correctAnswer;
        private Integer mark;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static public class OptionDTO {
        private String text;
        private boolean isCorrect;
    }

}

