package com.quang.smart_recipe.service;

import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${app.upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String uploadFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            return baseUrl + "/uploads/" + fileName;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Lỗi khi upload file: " + e.getMessage());
        }
    }
}
