package com.gis.servelq.services;

import com.gis.servelq.dto.CategoryUpdateRequest;
import com.gis.servelq.models.Category;
import com.gis.servelq.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(String id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    public Category create(Category category) {
        if (categoryRepository.existsByCode(category.getCode())) {
            throw new IllegalArgumentException("Category code already exists: " + category.getCode());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(String id, CategoryUpdateRequest updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        if (updatedCategory.getCode() != null && !updatedCategory.getCode().equals(existingCategory.getCode())) {
            if (categoryRepository.existsByCode(updatedCategory.getCode())) {
                throw new IllegalArgumentException("Category code already exists: " + updatedCategory.getCode());
            }
        }

        if (updatedCategory.getCode() != null && !updatedCategory.getCode().isBlank()) {
            existingCategory.setCode(updatedCategory.getCode());
        }

        if (updatedCategory.getName() != null && !updatedCategory.getName().isBlank()) {
            existingCategory.setName(updatedCategory.getName());
        }

        if (updatedCategory.getArabicName() != null && !updatedCategory.getArabicName().isBlank()) {
            existingCategory.setArabicName(updatedCategory.getArabicName());
        }

        existingCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}