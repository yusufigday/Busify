package com.yusufgun.busify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("The message was sent to RabbitMQ: exchange={}, routing={}, message={}",
                    exchange, routingKey, message);
        } catch (Exception e) {
            log.error("RabbitMQ failed to send message: {}", e.getMessage());
        }
    }
}