package com.rusudinu.consumer.listener;

import com.rusudinu.consumer.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(@Payload String message) {
        log.info("----------------------------------------");
        log.info("Received new message from RabbitMQ:");
        log.info("Message content: {}", message);
        log.info("----------------------------------------");
    }
} 