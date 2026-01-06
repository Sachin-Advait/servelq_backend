package com.gis.servelq.repository;

import com.gis.servelq.models.AnnouncementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<AnnouncementModel, UUID> {

    // Optional: fetch announcements for a user
    @Query("""
        SELECT DISTINCT a
        FROM AnnouncementModel a
        JOIN a.targetUser tu
        WHERE tu = :userId
    """)
    List<AnnouncementModel> findByTargetUser(@Param("userId") String userId);
}
