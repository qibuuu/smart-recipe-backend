package com.quang.smart_recipe.dto.response;

import lombok.Data;

@Data
public class IngredientResponseDTO {
    private Long id;
    private String name;
    private String unit;
    private String imageUrl;
}
