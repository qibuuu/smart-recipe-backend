package com.quang.smart_recipe.mapper;

import com.quang.smart_recipe.dto.response.FridgeItemResponseDTO;
import com.quang.smart_recipe.entity.UserFridge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserFridgeMapper {
    @Mapping(source = "ingredient.id", target = "ingredientId")
    @Mapping(source = "ingredient.name", target = "ingredientName")
    @Mapping(source = "ingredient.unit", target = "unit")
    FridgeItemResponseDTO toResponseDTO(UserFridge userFridge);
}
