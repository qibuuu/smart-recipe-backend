package com.quang.smart_recipe.dto.response;

import lombok.Data;

@Data
public class RecipeResponseDTO {
    private Long id;
    private String title;
    private String imageUrl;
    private String instructions;
    private String description;
    private String difficulty;
    private Integer prepTime;
    private Integer calories;
}
