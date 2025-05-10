package com.rusudinu.consumer.listener;

import com.rusudinu.consumer.config.RabbitMQConfig;
import com.rusudinu.consumer.model.ImageAnalysisResult;
import com.rusudinu.consumer.model.ImageMessage;
import com.rusudinu.consumer.service.ImageDownloadService;
import com.rusudinu.consumer.service.OllamaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final ImageDownloadService imageDownloadService;
    private final OllamaService ollamaService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(@Payload String imageUrl) {
        log.info("Received message with image URL: {}", imageUrl);

        try {
            byte[] imageBytes = imageDownloadService.downloadImage(imageUrl);

            ImageAnalysisResult result = ollamaService.analyzeImage(imageUrl, imageBytes);

            log.info("Image analysis result: URL={}, Category={}, Text={}",
                    result.getImageUrl(), 
                    result.getCategory(), 
                    result.getExtractedText() != null ? result.getExtractedText().substring(0, Math.min(50, result.getExtractedText().length())) + "..." : "None");

            if (result.getProcessingError() != null) {
                log.error("Processing error: {}", result.getProcessingError());
            }
        } catch (IOException e) {
            log.error("Error processing image from URL: {}", imageUrl, e);
        } catch (Exception e) {
            log.error("Unexpected error processing message", e);
        }
    }
}
