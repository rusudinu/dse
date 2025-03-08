package com.rusudinu.producer.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    public String sendMessage(@RequestBody String message) {
        log.info("Attempting to send message to Kafka topic 'messages': {}", message);
        
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("messages", message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to partition {} with offset {}", 
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to Kafka: {}", ex.getMessage(), ex);
            }
        });
        
        return "Message processing initiated: " + message;
    }
} 