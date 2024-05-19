package org.example.cookercorner.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    boolean isImageFile(MultipartFile image);
    String saveImage(MultipartFile image) throws FileUploadException;
}
