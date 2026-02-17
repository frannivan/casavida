package com.casavida.backend.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for image compression and optimization
 * Reduces storage requirements and improves load times
 */
public interface ImageCompressionService {
    
    /**
     * Compress an image while maintaining acceptable quality
     * 
     * @param originalImage Original image bytes
     * @param contentType MIME type (image/jpeg, image/png, etc.)
     * @return Compressed image bytes
     * @throws IllegalArgumentException if content type is not supported
     */
    byte[] compressImage(byte[] originalImage, String contentType);
    
    /**
     * Compress an uploaded image file
     * 
     * @param file Uploaded image file
     * @return Compressed image bytes
     * @throws IllegalArgumentException if file type is not supported
     * @throws RuntimeException if compression fails
     */
    byte[] compressImage(MultipartFile file);
}
