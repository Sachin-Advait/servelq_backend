package com.gis.servelq.dto;


import lombok.Data;

import java.time.Duration;
import java.util.Map;

@Data
public class SurveySubmissionRequest {
    private String userId;
    private Map<String, Object> answers;
    private Long finishTime;
}
