package com.gis.servelq.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tokenId;
    private String counterCode;
    private String agentName;

    private int rating; // 1–5
    private String review;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
