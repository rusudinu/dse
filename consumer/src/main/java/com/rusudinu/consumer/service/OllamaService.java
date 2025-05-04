package com.rusudinu.consumer.service;

import com.rusudinu.consumer.model.ImageAnalysisResult;
import com.rusudinu.consumer.model.ImageCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OllamaService {

    private static final String MODEL_NAME = "llava";

    @Value("${ollama.api.url:http://localhost:11434/api/generate}")
    private String ollamaApiUrl;

    private final RestTemplate restTemplate;

    public OllamaService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Analyzes an image using Ollama to classify it and extract text
     * 
     * @param imageUrl The URL of the image
     * @param imageBytes The bytes of the image
     * @return The analysis result containing the category and extracted text
     */
    public ImageAnalysisResult analyzeImage(String imageUrl, byte[] imageBytes) {
        log.info("Analyzing image from URL: {}", imageUrl);

        try {
            // Convert image to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Create request body for Ollama
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("stream", false);

            // Prompt for image classification and text extraction
            String prompt = "Analyze this image and do two things:\n" +
                    "1. Classify it into one of these categories: INGREDIENTS_LABEL (product ingredients label), " +
                    "NUTRITIONAL_TABLE (product nutritional table), ALCOHOL (alcohol bottle), " +
                    "CIGARETTES (cigarettes pack), MAYBE (blurry image), NONE (not a valid product image).\n" +
                    "2. Extract any visible text from the image.\n\n" +
                    "Format your response as: CATEGORY: [category name]\nTEXT: [extracted text]";

            requestBody.put("prompt", prompt);
            requestBody.put("images", new String[]{base64Image});

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make request to Ollama
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(ollamaApiUrl, request, Map.class);

            // Process response
            if (response != null && response.containsKey("response")) {
                String ollamaResponse = (String) response.get("response");
                log.info("Ollama response: {}", ollamaResponse);

                return parseOllamaResponse(imageUrl, ollamaResponse);
            } else {
                log.error("Invalid response from Ollama");
                return ImageAnalysisResult.builder()
                        .imageUrl(imageUrl)
                        .category(ImageCategory.NONE)
                        .processingError("Invalid response from Ollama")
                        .build();
            }
        } catch (Exception e) {
            log.error("Error analyzing image", e);
            return ImageAnalysisResult.builder()
                    .imageUrl(imageUrl)
                    .category(ImageCategory.NONE)
                    .processingError("Error analyzing image: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Parses the response from Ollama to extract the category and text
     * 
     * @param imageUrl The URL of the image
     * @param ollamaResponse The response from Ollama
     * @return The parsed analysis result
     */
    private ImageAnalysisResult parseOllamaResponse(String imageUrl, String ollamaResponse) {
        ImageCategory category = ImageCategory.NONE;
        String extractedText = "";

        try {
            // Extract category
            if (ollamaResponse.contains("CATEGORY:")) {
                String categoryPart = ollamaResponse.split("CATEGORY:")[1].split("\n")[0].trim();

                // Map the category string to our enum
                if (categoryPart.contains("INGREDIENTS_LABEL")) {
                    category = ImageCategory.INGREDIENTS_LABEL;
                } else if (categoryPart.contains("NUTRITIONAL_TABLE")) {
                    category = ImageCategory.NUTRITIONAL_TABLE;
                } else if (categoryPart.contains("ALCOHOL")) {
                    category = ImageCategory.ALCOHOL;
                } else if (categoryPart.contains("CIGARETTES")) {
                    category = ImageCategory.CIGARETTES;
                } else if (categoryPart.contains("MAYBE")) {
                    category = ImageCategory.MAYBE;
                } else {
                    category = ImageCategory.NONE;
                }
            }

            // Extract text
            if (ollamaResponse.contains("TEXT:")) {
                extractedText = ollamaResponse.split("TEXT:")[1].trim();
            }

            return ImageAnalysisResult.builder()
                    .imageUrl(imageUrl)
                    .category(category)
                    .extractedText(extractedText)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing Ollama response", e);
            return ImageAnalysisResult.builder()
                    .imageUrl(imageUrl)
                    .category(ImageCategory.NONE)
                    .extractedText("Error parsing response: " + e.getMessage())
                    .processingError("Error parsing Ollama response: " + e.getMessage())
                    .build();
        }
    }
}
