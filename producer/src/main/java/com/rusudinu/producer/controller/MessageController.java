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

	@PostMapping("{messagesToSend}")
	public String sendMessage(@PathVariable Long messagesToSend) {
		String imageUrl = "https://scanneralimente.ro/assets/marketing/home-vertical.png";
		try {
			rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, imageUrl);
		} catch (Exception e) {
			log.error("Error sending message to RabbitMQ", e);
			return "Error sending message: " + e.getMessage();
		}
		return "Image messages sent successfully";
	}
} 
