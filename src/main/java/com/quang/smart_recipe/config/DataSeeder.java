package com.quang.smart_recipe.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.entity.Recipe;
import com.quang.smart_recipe.entity.RecipeIngredient;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.repository.RecipeIngredientRepository;
import com.quang.smart_recipe.repository.RecipeRepository;
import com.quang.smart_recipe.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. TẠO TÀI KHOẢN ADMIN (ID = 1)
        if (userRepository.count() == 0) {
            log.info("Tạo tài khoản Admin...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEmail("admin@smartrecipe.com");
            admin.setRole("USER");
            userRepository.save(admin);
        }

        // 2. NẠP HÀNG NGÀN MÓN ĂN TỪ FILE JSON
        if (recipeRepository.count() == 0) {
            log.info("Bắt đầu đọc file recipes.json để nạp kho Ẩm thực...");

            try {
                // TỰ KHỞI TẠO OBJECT MAPPER BẰNG TAY Ở ĐÂY
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

                // Mở file recipes.json trong thư mục resources
                InputStream inputStream = new ClassPathResource("test-recipes.json").getInputStream();

                // Ép kiểu toàn bộ JSON thành List các Object Java
                List<RecipeSeedDTO> seedData = objectMapper.readValue(inputStream, new TypeReference<List<RecipeSeedDTO>>(){});

                Map<String, Ingredient> ingCache = new HashMap<>();
                List<RecipeIngredient> allRecipeIngredients = new ArrayList<>();

                // Duyệt qua từng món ăn trong file JSON
                for (RecipeSeedDTO dto : seedData) {

                    // Lưu Món ăn
                    Recipe newRecipe = new Recipe(null, dto.getTitle(), dto.getDescription(), dto.getInstructions(),
                            dto.getDifficulty(), dto.getPrepTime(), dto.getCalories(), dto.getServings() != null ? dto.getServings() : 4, dto.getImageUrl(),
                            1L, dto.getTags(), null);
                    Recipe savedRecipe = recipeRepository.save(newRecipe);

                    // Xử lý Nguyên liệu đi kèm
                    if (dto.getIngredients() != null) {
                        for (IngredientSeedDTO ingDto : dto.getIngredients()) {
                            String ingName = ingDto.getName().trim();

                            // Cơ chế Auto-Learning: Tìm trong Cache hoặc DB, không có thì tạo mới
                            Ingredient ing = ingCache.get(ingName);
                            if (ing == null) {
                                ing = ingredientRepository.findByNameIgnoreCase(ingName).orElse(null);
                                if (ing == null) {
                                    ing = ingredientRepository.save(new Ingredient(null, ingName, ingDto.getUnit(), ""));
                                }
                                ingCache.put(ingName, ing); // Lưu vào RAM để vòng lặp sau chộp luôn cho nhanh
                            }

                            // Tạo liên kết
                            allRecipeIngredients.add(new RecipeIngredient(null, savedRecipe, ing, ingDto.getAmount()));
                        }
                    }
                }

                // Lưu toàn bộ liên kết nguyên liệu 1 lần duy nhất
                recipeIngredientRepository.saveAll(allRecipeIngredients);
                log.info("Hoàn tất! Đã nạp thành công {} món ăn từ file JSON siêu tốc!", seedData.size());

            } catch (Exception e) {
                log.error("Lỗi khi đọc file JSON: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // CÁC CLASS DTO NỘI BỘ ĐỂ KHỚP VỚI FILE JSON
    // ==========================================

    @Data
    public static class RecipeSeedDTO {
        private String title;
        private String description;
        private String instructions;
        private String difficulty;
        private int prepTime;
        private int calories;
        private Integer servings;
        private String imageUrl;
        private String tags;
        private List<IngredientSeedDTO> ingredients;
    }

    @Data
    public static class IngredientSeedDTO {
        private String name;
        private float amount;
        private String unit;
    }
}