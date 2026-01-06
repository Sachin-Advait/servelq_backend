package com.gis.servelq.repository;


import com.gis.servelq.models.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementReadRepository
        extends JpaRepository<AnnouncementRead, Long> {

    boolean existsByUserIdAndAnnouncementId(String userId, UUID announcementId);

    List<AnnouncementRead> findByUserId(String userId);
}

