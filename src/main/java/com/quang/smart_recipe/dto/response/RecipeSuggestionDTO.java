package com.quang.smart_recipe.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RecipeSuggestionDTO {
    private Long recipeId;
    private String title;
    private String imageUrl;
    private String instructions;

    private int matchPercentage;
    private List<String> missingIngredients;
}
