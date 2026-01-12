package com.gis.servelq.dto;

import lombok.Data;

@Data
public class UpdateFCMTokenRequest {
    private String userId;
    private String fcmToken;
}
