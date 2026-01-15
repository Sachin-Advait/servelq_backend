package com.gis.servelq.dto;


import com.gis.servelq.models.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRequest {
    public String fcmToken = null;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotNull
    private UserRole role = UserRole.USER;
    private String branchId;
    private String counterId;
}

