package com.gis.servelq.services;

import com.gis.servelq.dto.TokenRequest;
import com.gis.servelq.dto.TokenResponseDTO;
import com.gis.servelq.models.Branch;
import com.gis.servelq.models.Services;
import com.gis.servelq.models.Token;
import com.gis.servelq.models.TokenStatus;
import com.gis.servelq.repository.BranchRepository;
import com.gis.servelq.repository.ServiceRepository;
import com.gis.servelq.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final ServiceRepository serviceRepository;
    private final BranchRepository branchRepository;
    private final SocketService socketService;
    private final AgentService agentService;

    @Transactional
    public TokenResponseDTO generateToken(TokenRequest request) {
        int attempts = 0;
        int maxAttempts = 3;

        while (true) {
            try {
                return createTokenOnce(request);
            } catch (DataIntegrityViolationException e) {
                if (++attempts >= maxAttempts) {
                    throw new RuntimeException("Failed to generate token after " + maxAttempts + " attempts", e);
                }
                // Optional: small delay between retries to reduce collision
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Token generation interrupted", ie);
                }
            }
        }
    }

    private TokenResponseDTO createTokenOnce(TokenRequest request) {
        Services service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        String tokenNumber = generateTokenNumber(branch);

        Token token = new Token();
        token.setToken(tokenNumber);
        token.setBranchId(branch.getId());
        token.setServiceId(service.getId());
        token.setServiceName(service.getName());

        if (request.getPriority() != null) {
            token.setPriority(request.getPriority());
        }

        token.setStatus(TokenStatus.WAITING);
        token.setMobileNumber(request.getMobileNumber());

        if (request.getCounterIds() != null) {
            token.setCounterIds(request.getCounterIds());
        }

        Token savedToken = tokenRepository.saveAndFlush(token);

        if (request.getCounterIds() != null) {
            for (String counterId : request.getCounterIds()) {
                agentService.notifyAgentUpcoming(counterId);
            }
        }

        socketService.tvSocket(request.getBranchId());
        return TokenResponseDTO.fromEntity(savedToken);
    }

    public Token getTokenById(String tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    private String generateTokenNumber(Branch branch) {
        LocalDate today = LocalDate.now();

        Optional<Integer> lastNumber = tokenRepository.findLastTokenNumberForToday(
                branch.getId(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        int nextNumber = lastNumber.orElse(1000) + 1;
        return String.valueOf(nextNumber);
    }
}
