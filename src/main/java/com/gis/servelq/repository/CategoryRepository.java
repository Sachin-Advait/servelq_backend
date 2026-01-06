package com.gis.servelq.repository;

import com.gis.servelq.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByPriority(Integer priority);

    boolean existsByCode(String code);
}