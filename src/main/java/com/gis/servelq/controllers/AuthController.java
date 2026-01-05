package com.gis.servelq.controllers;

import com.gis.servelq.dto.LoginRequest;
import com.gis.servelq.dto.RegisterRequest;
import com.gis.servelq.dto.UserResponseDTO;
import com.gis.servelq.models.User;
import com.gis.servelq.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serveiq/api/auth")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterRequest dto) {
        User user = userService.registerUser(dto);
        return new UserResponseDTO(user);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody LoginRequest dto) {
        User user = userService.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new UserResponseDTO(user);
    }
}
