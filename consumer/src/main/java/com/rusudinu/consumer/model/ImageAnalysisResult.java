package com.rusudinu.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisResult {
    private String imageUrl;
    private ImageCategory category;
    private String extractedText;
    private String processingError; // In case of errors during processing
}
