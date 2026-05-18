package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.IngredientRequestDTO;
import com.quang.smart_recipe.dto.response.IngredientResponseDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.mapper.IngredientMapper;
import com.quang.smart_recipe.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    // requestDTO -> Entity
    public IngredientResponseDTO createIngredient(IngredientRequestDTO request) {
        Ingredient newEntity = ingredientMapper.toEntity(request);
        Ingredient savedEntity = ingredientRepository.save(newEntity);
        return ingredientMapper.toResponseDTO(savedEntity);
    }

    public List<IngredientResponseDTO> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        
        if (request.getName() != null) ingredient.setName(request.getName());
        if (request.getUnit() != null) ingredient.setUnit(request.getUnit());
        if (request.getImageUrl() != null) ingredient.setImageUrl(request.getImageUrl());
        
        Ingredient saved = ingredientRepository.save(ingredient);
        return ingredientMapper.toResponseDTO(saved);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found");
        }
        ingredientRepository.deleteById(id);
    }
}
