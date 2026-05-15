package com.quang.smart_recipe.mapper;

import com.quang.smart_recipe.dto.request.RecipeRequestDTO;
import com.quang.smart_recipe.dto.response.RecipeResponseDTO;
import com.quang.smart_recipe.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    @Mapping(target = "totalTime", source = "prepTime")
    RecipeResponseDTO toResponseDTO(Recipe recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipeIngredients", ignore = true)
    Recipe toEntity(RecipeRequestDTO requestDTO);
}
