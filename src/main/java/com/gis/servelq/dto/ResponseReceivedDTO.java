package com.gis.servelq.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.gis.servelq.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseReceivedDTO {
    private String id;
    private String username;
    private UserRole role;
    private String position;
    private Instant submittedAt;
    private String result;
}
