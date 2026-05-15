package com.quang.smart_recipe.dto.request;

import lombok.Data;

@Data
public class RecipeIngredientRequestDTO {
    private String ingredientName;
    private Float amount;
    private String unit;
}