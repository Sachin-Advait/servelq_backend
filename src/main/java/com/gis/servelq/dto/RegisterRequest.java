package com.gis.servelq.dto;


import com.gis.servelq.models.KioskCategory;
import com.gis.servelq.models.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotNull
    private UserRole role = UserRole.USER;
    private KioskCategory category;
    private String branchId;
    private String counterId;
    public String fcmToken;
}

