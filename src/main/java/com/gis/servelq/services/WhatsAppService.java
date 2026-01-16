package com.gis.servelq.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class WhatsAppService {

    @Value("${twilio.sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Value("${twilio.from}")
    private String from;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public String sendMessage(String to, String messageText) {
        try {
            log.info("Sending WhatsApp message (sync) to: {}", to);

            Message msg = Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber(from),
                    messageText
            ).create();

            log.info("WhatsApp message sent successfully. SID: {}", msg.getSid());
            return msg.getSid();

        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}: {}", to, e.getMessage(), e);
            return null;
        }
    }
}
