package com.gis.servelq.dto;

import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
public class UserTrainingDTO {

    private Long assignmentId;
    private Long trainingId;

    private String title;
    private String type;
    private String duration;

    // Cloudinary
    private String cloudinaryUrl;
    private String cloudinaryFormat;
    private String cloudinaryResourceType;

    // User-specific
    private int progress;
    private String status;
    private Instant dueDate;
}
