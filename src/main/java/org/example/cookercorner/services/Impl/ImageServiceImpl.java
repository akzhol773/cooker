package org.example.cookercorner.services.Impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.services.ImageService;
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
            Path uploadPath = Paths.get("recipe_images");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(image.getOriginalFilename());
            Files.write(filePath, image.getBytes());

            return filePath.toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload image: " + e.getMessage(), e);
        }
    }
}
