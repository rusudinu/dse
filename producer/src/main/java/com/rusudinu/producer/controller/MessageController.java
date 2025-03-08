package com.rusudinu.producer.controller;

import com.rusudinu.producer.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public String sendMessage(@RequestBody String message) {
        log.info("Attempting to send message to RabbitMQ queue 'messages': {}", message);
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
            log.info("Message sent successfully to RabbitMQ queue");
            return "Message sent successfully: " + message;
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            return "Failed to send message: " + e.getMessage();
        }
    }
} 