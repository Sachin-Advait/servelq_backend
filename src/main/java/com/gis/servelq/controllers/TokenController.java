package com.gis.servelq.controllers;

import com.gis.servelq.dto.*;
import com.gis.servelq.models.Token;
import com.gis.servelq.repository.TokenRepository;
import com.gis.servelq.services.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    @PostMapping("/generate")
    public ResponseEntity<TokenResponse> generateToken(@Valid @RequestBody TokenRequest request) {
        TokenResponse response = tokenService.generateToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<TokenDTO> getToken(@PathVariable String tokenId) {
        Token token = tokenService.getTokenById(tokenId);
        return ResponseEntity.ok(TokenDTO.fromEntity(token));
    }
}