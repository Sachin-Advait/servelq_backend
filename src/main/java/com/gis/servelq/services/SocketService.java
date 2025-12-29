package com.gis.servelq.services;

import com.gis.servelq.dto.TVDisplayResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final TVDisplayService tvDisplayService;

    public void broadcast(String destination, Object payload) {
        log.info("🔥 WEBSOCKET PUSH → destination={}", destination);
        log.debug("WEBSOCKET payload={}", payload);
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void tvSocket(String branchId) {
        TVDisplayResponseDTO data = tvDisplayService.getTVDisplayData(branchId);
        broadcast("/topic/tv/" + branchId, data);
    }
}
