package com.gis.servelq.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingEngagementDTO {

    private String userId;      // stays String (external user/JWT)
    private String learner;

    private Long trainingId;    // ✅ SQL FK
    private String video;       // training title

    private int progress;
    private String status;
}