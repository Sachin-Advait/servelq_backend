package com.gis.servelq.services;

import com.gis.servelq.dto.TVDisplayResponseDTO;
import com.gis.servelq.models.Branch;
import com.gis.servelq.models.Token;
import com.gis.servelq.models.TokenStatus;
import com.gis.servelq.repository.BranchRepository;
import com.gis.servelq.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TVDisplayService {
    private final TokenRepository tokenRepository;
    private final BranchRepository branchRepository;

    public TVDisplayResponseDTO getTVDisplayData(String branchId) {

        Branch branch = branchRepository.findByIdAndEnabledTrue(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found or disabled"));

        // Get latest called tokens (last 4)
        List<Token> latestCalled = tokenRepository.findLatestCalledTokens(branchId)
                .stream().limit(4).toList();

        // Get currently serving tokens
        List<Token> nowServing = tokenRepository.findByBranchIdAndStatusOrderByPriorityAscCreatedAtAsc(
                branchId, TokenStatus.SERVING);

        // Get upcoming tokens
        List<Token> upcoming = tokenRepository.findByBranchIdAndStatusOrderByPriorityAscCreatedAtAsc(
                        branchId, TokenStatus.WAITING)
                .stream().limit(10).toList();

        var counts = tokenRepository.countByBranchGrouped(branchId);

        long waitingCount = 0;
        long servingCount = 0;
        long noShowCount = 0;
        long completedCount = 0;

        for (Object[] row : counts) {
            TokenStatus status = (TokenStatus) row[0];
            long count = (long) row[1];

            switch (status) {
                case WAITING -> waitingCount = count;
                case SERVING -> servingCount = count;
                case NO_SHOW -> noShowCount = count;
                case DONE -> completedCount = count;
            }
        }

        TVDisplayResponseDTO response = new TVDisplayResponseDTO();
        response.setBranchName(branch.getName());

        // latest calls
        response.setLatestCalls(latestCalled.stream().map(token -> {
            TVDisplayResponseDTO.DisplayToken dt = new TVDisplayResponseDTO.DisplayToken();
            dt.setToken(token.getToken());
            dt.setCounter(token.getAssignedCounterName());
            dt.setService(token.getServiceName());
            return dt;
        }).collect(Collectors.toList()));

        // serving list with counter + service
        response.setNowServing(nowServing.stream().map(token -> {
            TVDisplayResponseDTO.DisplayToken dt = new TVDisplayResponseDTO.DisplayToken();
            dt.setToken(token.getToken());
            dt.setCounter(token.getAssignedCounterName());
            dt.setService(token.getServiceName());
            return dt;
        }).collect(Collectors.toList()));

        // upcoming only tokens
        response.setUpcomingTokens(upcoming.stream()
                .map(Token::getToken)
                .collect(Collectors.toList()));

        // ✔ NEW — set statistics
        response.setWaiting(waitingCount);
        response.setServing(servingCount);
        response.setNoShow(noShowCount);
        response.setServedToday(completedCount);

        return response;
    }
}
