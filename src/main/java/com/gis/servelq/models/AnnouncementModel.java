package com.gis.servelq.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AnnouncementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(columnDefinition = "text")
    private String message;

    @ElementCollection
    @CollectionTable(
            name = "announcement_targets",
            joinColumns = @JoinColumn(name = "announcement_id")
    )
    @Column(name = "user_id")
    private Set<String> targetUser = new HashSet<>();

    @CreatedDate
    private Instant createdAt;
}
