package com.gis.servelq.repository;

import com.gis.servelq.models.TvContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TvContentRepository extends JpaRepository<TvContent, String> {
    List<TvContent> findByBranchId(String branchId);
}