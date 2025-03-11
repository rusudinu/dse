package com.rusudinu.producer.controller;

import com.rusudinu.producer.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

	private final RabbitTemplate rabbitTemplate;

	@PostMapping("{messagesToSend}")
	public String sendMessage(@RequestBody String message, @PathVariable Long messagesToSend) {
		List<String> messages = new ArrayList<>();
		for (int i = 0; i < messagesToSend; i++) {
			messages.add(message + " [" + i + "]");
		}

		log.info("Finished preparing messages to send");

		messages.parallelStream().forEach(s -> rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, s));
		return "Messages sent successfully";
	}
} 
