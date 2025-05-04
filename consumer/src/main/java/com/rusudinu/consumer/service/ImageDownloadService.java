package com.rusudinu.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class ImageDownloadService {

    /**
     * Downloads an image from a URL and returns its bytes
     * 
     * @param imageUrl The URL of the image to download
     * @return The image bytes
     * @throws IOException If an error occurs during download
     */
    public byte[] downloadImage(String imageUrl) throws IOException {
        log.info("Downloading image from URL: {}", imageUrl);
        
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            byte[] imageBytes = outputStream.toByteArray();
            log.info("Successfully downloaded image: {} bytes", imageBytes.length);
            return imageBytes;
        } finally {
            connection.disconnect();
        }
    }
}
