package org.example.cookercorner.services.Impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public boolean isImageFile(MultipartFile image) {
        String contentType = image.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @Override
    public String saveImage(MultipartFile image) throws FileUploadException {
        try {
            // Define the upload directory
            Path uploadPath = Paths.get("recipe_images");

            // Ensure the uploads directory exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file to the local file system
            Path filePath = uploadPath.resolve(image.getOriginalFilename());
            Files.write(filePath, image.getBytes());

            // Return the file path as a string
            return filePath.toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload image: " + e.getMessage(), e);
        }
    }
}
