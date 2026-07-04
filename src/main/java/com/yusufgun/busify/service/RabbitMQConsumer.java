package com.yusufgun.busify.service;

import com.yusufgun.busify.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TICKET)
    public void handleTicketMessage(Map<String, Object> message) {
        log.info("Ticket message received: {}", message);

        String action = (String) message.get("action");
        String email = (String) message.get("email");

        switch (action) {
            case "TICKET_PURCHASED":
                log.info("Sending ticket confirmation email to {}...", email);
                break;
            case "TICKET_CANCELLED":
                log.info("Sending cancellation email to {}...", email);
                break;
            default:
                log.warn("Unknown ticket action: {}", action);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USER)
    public void handleUserMessage(Map<String, Object> message) {
        log.info("User message received: {}", message);

        String action = (String) message.get("action");
        String email = (String) message.get("email");

        if ("USER_REGISTERED".equals(action)) {
            log.info("Sending welcome email to {}...", email);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUTH)
    public void handleAuthMessage(Map<String, Object> message) {
        log.info("Auth message received: {}", message);

        String action = (String) message.get("action");
        String email = (String) message.get("email");

        if ("PASSWORD_RESET".equals(action)) {
            String token = (String) message.get("resetToken");
            log.info("Sending password reset link to {}: token={}", email, token);
        }
    }
}
