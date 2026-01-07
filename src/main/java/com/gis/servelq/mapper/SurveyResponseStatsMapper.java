package com.gis.servelq.mapper;

import com.gis.servelq.dto.SurveyResponseStatsDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyResponseStatsMapper {

    public SurveyResponseStatsDTO.OverallStatsDTO toOverallStats(int totalInvited, int totalResponded, double responseRate) {
        return SurveyResponseStatsDTO.OverallStatsDTO.builder()
                .totalInvited(totalInvited)
                .totalResponded(totalResponded)
                .overallResponseRate(responseRate)
                .build();
    }

    public SurveyResponseStatsDTO.BreakdownByRoleDTO toRoleBreakdown(String position, int responded, int invited, double rate) {
        return SurveyResponseStatsDTO.BreakdownByRoleDTO.builder()
                .position(position)
                .responded(responded)
                .invited(invited)
                .responseRate(rate)
                .build();
    }




}

