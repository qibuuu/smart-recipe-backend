package com.quang.smart_recipe.service;

import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
    );

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Tệp tin tải lên bị rỗng!");
        }

        // 1. Validate File Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Định dạng tệp không hợp lệ! Chỉ cho phép tệp JPG, JPEG, PNG, hoặc GIF.");
        }

        try {
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Validate original filename to prevent path traversal
            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.contains("..") || originalName.contains("/") || originalName.contains("\\")) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Tên tệp chứa ký tự không hợp lệ!");
            }

            // 3. Extract Extension securely and build safe random UUID name
            String ext = ".jpg";
            if (originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }

            String safeFileName = UUID.randomUUID().toString() + ext;
            Path filePath = uploadPath.resolve(safeFileName).normalize();

            // 4. Double check that path resolution did not escape upload directory
            if (!filePath.startsWith(uploadPath)) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Đường dẫn lưu tệp không hợp lệ!");
            }

            file.transferTo(filePath.toFile());

            return baseUrl + "/uploads/" + safeFileName;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Lỗi khi upload file: " + e.getMessage());
        }
    }
}
