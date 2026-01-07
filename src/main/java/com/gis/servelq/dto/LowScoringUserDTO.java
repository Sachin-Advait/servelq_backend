package com.gis.servelq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowScoringUserDTO {
    private String userId;
    private String username;
    private Double avgPercentage;
    private List<String> attemptedQuizzes;
    private int attemptCount;
}