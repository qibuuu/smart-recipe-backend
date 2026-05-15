package com.quang.smart_recipe.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RecipeCreateRequestDTO {
    private String title;
    private String description;
    private String difficulty;
    private Integer prepTime;
    private Integer calories;
    private Integer servings;
    private String imageUrl;
    private String tags;
    private String instructions;
    private List<RecipeIngredientRequestDTO> ingredients;
}