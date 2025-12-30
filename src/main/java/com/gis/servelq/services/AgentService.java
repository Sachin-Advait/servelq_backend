package com.gis.servelq.services;

import com.gis.servelq.dto.*;
import com.gis.servelq.models.Counter;
import com.gis.servelq.models.CounterStatus;
import com.gis.servelq.models.Token;
import com.gis.servelq.models.TokenStatus;
import com.gis.servelq.repository.CounterRepository;
import com.gis.servelq.repository.ServiceRepository;
import com.gis.servelq.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final TokenRepository tokenRepository;
    private final CounterRepository counterRepository;
    private final ServiceRepository serviceRepository;
    private final SocketService socketService;
    private final CounterService counterService;

    public List<TokenResponseDTO> getUpcomingTokensForCounter(String counterId) {
        return tokenRepository.findUpcomingTokensForCounter(counterId)
                .stream()
                .map(TokenResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AgentCallResponseDTO callNextToken(String counterId) {
        Counter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("Counter not found"));

        if (counter.getPaused()) {
            throw new RuntimeException("Counter is paused");
        }
        serviceRepository.findById(counter.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Optional<Token> optionalToken = tokenRepository.findNextToken(counterId);

        if (optionalToken.isEmpty()) {
            throw new RuntimeException("No tokens available");
        }

        Token nextToken = optionalToken.get();

        nextToken.setStatus(TokenStatus.CALLING);
        nextToken.setAssignedCounterId(counterId);
        nextToken.setAssignedCounterName(counter.getName());
        tokenRepository.save(nextToken);

        counter.setStatus(CounterStatus.CALLING);
        counterRepository.save(counter);

        socketService.tvSocket(nextToken.getBranchId());
        notifyAgentUpcoming(counterId);

        return AgentCallResponseDTO.fromEntity(nextToken);
    }

    @Transactional
    public AgentCallResponseDTO callHoldToken(String tokenId) {
        Token token = tokenRepository.findById(tokenId).orElseThrow(() -> new RuntimeException("Token not found"));

        Counter counter = counterRepository.findById(token.getAssignedCounterId()).orElseThrow(() -> new RuntimeException("Counter not found"));

        if (token.getStatus() != TokenStatus.HOLD) {
            throw new RuntimeException("Token cannot be called from status: " + token.getStatus());
        }

        token.setStatus(TokenStatus.CALLING);
        tokenRepository.save(token);

        counter.setStatus(CounterStatus.CALLING);
        counterRepository.save(counter);

        socketService.tvSocket(token.getBranchId());
        notifyAgentUpcoming(counter.getId());

        return AgentCallResponseDTO.fromEntity(token);
    }

    @Transactional
    public void startServingToken(String tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getStatus() != TokenStatus.CALLING) return;

        token.setStatus(TokenStatus.SERVING);
        token.setStartAt(LocalDateTime.now());
        tokenRepository.save(token);

        if (token.getAssignedCounterId() != null) {
            Counter counter = counterRepository.findById(token.getAssignedCounterId())
                    .orElseThrow(() -> new RuntimeException("Counter not found"));
            counter.setStatus(CounterStatus.SERVING);
            counterRepository.save(counter);

            notifyAgentUpcoming(counter.getId());
        }

        socketService.tvSocket(token.getBranchId());
    }

    @Transactional
    public void completeToken(String tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        token.setStatus(TokenStatus.REVIEW);
        token.setEndAt(LocalDateTime.now());
        tokenRepository.save(token);

        if (token.getAssignedCounterId() != null) {
            Counter counter = counterRepository.findById(token.getAssignedCounterId())
                    .orElseThrow(() -> new RuntimeException("Counter not found"));
            counter.setStatus(CounterStatus.COMPLETE);
            counterRepository.save(counter);

            notifyAgentUpcoming(counter.getId());
        }

        socketService.tvSocket(token.getBranchId());
    }

    public List<RecentServiceDTO> getRecentServices(String counterId) {
        return tokenRepository.findTop20ByStatusAndAssignedCounterIdOrderByEndAtDesc(TokenStatus.DONE, counterId)
                .stream()
                .map(token -> RecentServiceDTO.fromEntity(
                        token.getToken(),
                        token.getMobileNumber(),
                        token.getServiceName(),
                        token.getStartAt(),
                        token.getEndAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public AgentCallResponseDTO recallToken(String tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        token.setStatus(TokenStatus.CALLING);
        tokenRepository.save(token);

        Counter counter = counterRepository.findById(token.getAssignedCounterId())
                .orElseThrow(() -> new RuntimeException("Counter not found"));
        counter.setStatus(CounterStatus.CALLING);
        counterRepository.save(counter);

        notifyAgentUpcoming(counter.getId());
        socketService.tvSocket(token.getBranchId());

        return AgentCallResponseDTO.fromEntity(token);
    }

    @Transactional
    public Token transferToken(TokenTransferRequest request) {
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getStatus() == TokenStatus.DONE || token.getStatus() == TokenStatus.REVIEW ||
            token.getStatus() == TokenStatus.HOLD ||
            token.getStatus() == TokenStatus.NO_SHOW) {
            throw new RuntimeException("Token cannot be transferred from status: " + token.getStatus());
        }

        Counter toCounter = counterRepository.findById(request.getToCounterId())
                .orElseThrow(() -> new RuntimeException("Target counter not found"));

        Counter fromCounter = counterRepository.findById(token.getAssignedCounterId())
                .orElseThrow(() -> new RuntimeException("Target counter not found"));

        fromCounter.setStatus(CounterStatus.IDLE);
        counterRepository.save(fromCounter);

        String fromCounterId = token.getAssignedCounterId();


        token.setIsTransfer(true);
        token.setTransferFrom(fromCounterId);

        // -----------------------------------------
        // REMOVE OLD COUNTER FROM counterIds LIST
        // -----------------------------------------
        if (token.getCounterIds() != null && fromCounterId != null) {
            token.getCounterIds().remove(fromCounterId);
        }
        // -----------------------------------------

        // ADD NEW COUNTER IF YOU WANT (optional)
        if (token.getCounterIds() != null) {
            token.getCounterIds().add(toCounter.getId());
        }

        // Set the assigned counter to new one
        token.setAssignedCounterId(toCounter.getId());
        token.setAssignedCounterName(toCounter.getName());

        token.setStatus(TokenStatus.WAITING);
        token.setStartAt(null);
        token.setEndAt(null);

        Token updated = tokenRepository.save(token);

        socketService.tvSocket(token.getBranchId());
        notifyAgentUpcoming(toCounter.getId());
        notifyAgentUpcoming(fromCounter.getId());

        return updated;
    }

    @Transactional
    public Token holdToken(String tokenId) {
        Token token = tokenRepository.findById(tokenId).orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getStatus() != TokenStatus.SERVING) {
            throw new RuntimeException("Token cannot be Hold from status: " + token.getStatus());
        }

        Counter counter = counterRepository.findById(token.getAssignedCounterId())
                .orElseThrow(() -> new RuntimeException("Target counter not found"));

        counter.setStatus(CounterStatus.IDLE);
        counterRepository.save(counter);

        token.setStatus(TokenStatus.HOLD);
        Token updated = tokenRepository.save(token);

        socketService.tvSocket(token.getBranchId());
        notifyAgentUpcoming(counter.getId());

        return updated;
    }

    public AgentCallResponseDTO getServingOrCallingTokenByCounter(String counterId) {
        Optional<Token> serving = tokenRepository
                .findFirstByAssignedCounterIdAndStatus(counterId, TokenStatus.SERVING);
        Optional<Token> calling = tokenRepository
                .findFirstByAssignedCounterIdAndStatus(counterId, TokenStatus.CALLING);

        Token token = serving.or(() -> calling).orElseThrow(
                () -> new RuntimeException("No serving or calling token found")
        );

        counterRepository.findById(counterId).orElseThrow(() -> new RuntimeException("Counter not found"));
        return AgentCallResponseDTO.fromEntity(token);
    }

    public void notifyAgentUpcoming(String counterId) {
        List<TokenResponseDTO> data = getUpcomingTokensForCounter(counterId);
        socketService.broadcast("/topic/agent-upcoming/" + counterId, data);
        
        CounterStatusResponseDTO counterData = counterService.getCounterStatusDetails(counterId);
        System.out.println("this is the counter Data for the display" + counterData);

        socketService.broadcast("/topic/counter-display/" + counterId, counterData);
    }
}
