package com.quang.smart_recipe.mapper;

import com.quang.smart_recipe.dto.request.IngredientRequestDTO;
import com.quang.smart_recipe.dto.response.IngredientResponseDTO;
import com.quang.smart_recipe.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    IngredientResponseDTO toResponseDTO(Ingredient ingredient);

    @Mapping(target = "id", ignore = true)
    Ingredient toEntity(IngredientRequestDTO requestDTO);
}
