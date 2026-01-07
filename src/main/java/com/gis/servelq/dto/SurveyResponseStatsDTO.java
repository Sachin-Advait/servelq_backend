package com.gis.servelq.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SurveyResponseStatsDTO {
    private OverallStatsDTO overall;
    private String title;

    @Builder
    @Data
    public static class OverallStatsDTO {
        private int totalInvited;
        private int totalResponded;
        private double overallResponseRate;
    }


    @Builder
    @Data
    public static class BreakdownByRoleDTO {
        private String position;
        private int invited;
        private int responded;
        private double responseRate;
    }
}

