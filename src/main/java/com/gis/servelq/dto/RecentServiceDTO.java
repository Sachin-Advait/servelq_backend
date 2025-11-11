package com.gis.servelq.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentServiceDTO {
    private String token;
    private String civilId;
    private String serviceName;
    private Long timeTakenInMinutes; // changed to minutes

    public static RecentServiceDTO fromEntity(String token, String civilId, String serviceName, LocalDateTime startAt, LocalDateTime endAt) {
        RecentServiceDTO dto = new RecentServiceDTO();
        dto.setToken(token);
        dto.setCivilId(civilId);
        dto.setServiceName(serviceName);

        if (startAt != null && endAt != null) {
            // calculate duration in minutes
            dto.setTimeTakenInMinutes(java.time.Duration.between(startAt, endAt).toMinutes());
        } else {
            dto.setTimeTakenInMinutes(null);
        }

        return dto;
    }
}
