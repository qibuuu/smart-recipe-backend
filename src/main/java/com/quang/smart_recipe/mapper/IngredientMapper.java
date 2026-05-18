package com.quang.smart_recipe.mapper;

import com.quang.smart_recipe.dto.request.IngredientRequestDTO;
import com.quang.smart_recipe.dto.response.IngredientResponseDTO;
import com.quang.smart_recipe.entity.Ingredient;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {

    public IngredientResponseDTO toResponseDTO(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setUnit(ingredient.getUnit());
        dto.setImageUrl(ingredient.getImageUrl());
        return dto;
    }

    public Ingredient toEntity(IngredientRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Ingredient entity = new Ingredient();
        entity.setName(requestDTO.getName());
        entity.setUnit(requestDTO.getUnit());
        entity.setImageUrl(requestDTO.getImageUrl());
        return entity;
    }
}
