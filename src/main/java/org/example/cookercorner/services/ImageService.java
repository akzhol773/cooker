package org.example.cookercorner.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    boolean isImageFile(MultipartFile image);

    String uploadFile(MultipartFile file, String folderName);


}
