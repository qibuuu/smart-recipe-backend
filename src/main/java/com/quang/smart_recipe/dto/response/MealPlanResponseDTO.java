package com.quang.smart_recipe.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanResponseDTO {
    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private String recipeImageUrl;
    private String dayOfWeek;
    private String mealType;
}
