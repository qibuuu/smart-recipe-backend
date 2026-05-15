package com.quang.smart_recipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDTO {
    private Long id;
    private String title;
    private String imageUrl;
    private String instructions;
    private String description;
    private String difficulty;
    private Integer prepTime;
    private Integer calories;
    private Integer servings;
    private Integer totalTime;
    private String tags;
    private String authorId;
    private List<String> ingredients;
}
