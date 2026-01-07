package com.gis.servelq.dto;

import com.gis.servelq.models.KioskCategory;
import com.gis.servelq.models.User;
import com.gis.servelq.models.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private UserRole role;
    private KioskCategory category;
    private String branchId;
    private String counterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.branchId = user.getBranchId();
        if (user.getCounterId() != null) this.counterId = user.getCounterId();
        if (user.getCategory() != null) this.category = user.getCategory();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user);
    }
}
