package com.quang.smart_recipe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    // THÊM VÀO CONTROLLER (VD: FileController hoặc RecipeController)
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            // Tạo thư mục uploads nếu chưa có
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }

            // Đổi tên file để không bị trùng (dùng thời gian hiện tại)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            java.nio.file.Path filePath = uploadPath.resolve(fileName);

            // Lưu file
            file.transferTo(filePath.toFile());

            // Trả về đường dẫn để Frontend dùng (Giả sử BE chạy port 8080)
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi upload file: " + e.getMessage());
        }
    }
}
