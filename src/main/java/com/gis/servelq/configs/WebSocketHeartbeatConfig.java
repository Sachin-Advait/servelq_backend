package com.gis.servelq.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketHeartbeatConfig {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send heartbeat every 60 seconds to all TV display topics
     * This keeps the connection alive and lets clients know the server is responsive
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void sendHeartbeat() {
        try {
            // Send to all branches - or make this dynamic based on active subscriptions
            messagingTemplate.convertAndSend("/topic/heartbeat", "PING");
            log.debug("Heartbeat sent to TV displays");
        } catch (Exception e) {
            log.error("Failed to send heartbeat", e);
        }
    }
}
