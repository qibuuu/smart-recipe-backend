package com.quang.smart_recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanRequestDTO {
    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    @NotBlank(message = "Meal type is required")
    private String mealType;
}
