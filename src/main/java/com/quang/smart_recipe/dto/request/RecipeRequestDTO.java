package com.quang.smart_recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecipeRequestDTO {
    @NotBlank(message = "Tên món ăn không được để trống!")
    private String title;

    private String description;
    private String difficulty;
    private Integer prepTime;
    private Integer calories;
    private String imageUrl;
    private String tags;
    private String authorId;

    @NotBlank(message = "Hướng dẫn không được để trống!")
    private String instructions;
}
