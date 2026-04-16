package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.response.RecipeSuggestionDTO;
import com.quang.smart_recipe.dto.request.RecipeRequestDTO;
import com.quang.smart_recipe.dto.response.RecipeResponseDTO;
import com.quang.smart_recipe.entity.Recipe;
import com.quang.smart_recipe.entity.RecipeIngredient;
import com.quang.smart_recipe.entity.UserFridge;
import com.quang.smart_recipe.mapper.RecipeMapper;
import com.quang.smart_recipe.repository.RecipeIngredientRepository;
import com.quang.smart_recipe.repository.RecipeRepository;
import com.quang.smart_recipe.repository.UserFridgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserFridgeRepository userFridgeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeMapper recipeMapper;

    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll()
                .stream() // Mở luồng dữ liệu
                .map(recipeMapper::toResponseDTO) //MapStruct chuyển Entity -> DTO
                .collect(Collectors.toList()); // Đóng gói lại thành List
    }

    // gợi ý món ăn
    public List<RecipeSuggestionDTO> suggestRecipesForUser(Long userId) {
        List<UserFridge> myFridge =  userFridgeRepository.findByUserId(userId);
        List<Long> myIngredientIds = new ArrayList<>();
        for (UserFridge item : myFridge) {
            myIngredientIds.add(item.getIngredient().getId());
        }

        List<Recipe> allRecipes = recipeRepository.findAll();
        List<RecipeSuggestionDTO> suggestions = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> requiredIngredients
                    = recipeIngredientRepository.findByRecipeId(recipe.getId());
            if (requiredIngredients.isEmpty()) continue;

            int matchCount = 0;
            List<String> missingList = new ArrayList<>();

            for (RecipeIngredient req : requiredIngredients) {
                Long requiedId = req.getIngredient().getId();

                if (myIngredientIds.contains(requiedId)) {
                    matchCount++;
                } else {
                    missingList.add(req.getIngredient().getName());
                }
            }

            int matchPercentage = (matchCount * 100) / requiredIngredients.size();

            if (matchPercentage > 0) {
                RecipeSuggestionDTO dto = new RecipeSuggestionDTO();
                dto.setRecipeId(recipe.getId());
                dto.setTitle(recipe.getTitle());
                dto.setImageUrl(recipe.getImageUrl());
                dto.setInstructions(recipe.getInstructions());
                dto.setMatchPercentage(matchPercentage);
                dto.setMissingIngredients(missingList);

                suggestions.add(dto);
            }
        }

        return suggestions;
    }

    public RecipeResponseDTO createRecipe(RecipeRequestDTO requestDTO) {
        Recipe recipe = recipeMapper.toEntity(requestDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toResponseDTO(savedRecipe);
    }

    public RecipeResponseDTO updateRecipe(Long id, RecipeRequestDTO request) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));

        existingRecipe.setTitle(request.getTitle());
        existingRecipe.setImageUrl(request.getImageUrl());
        existingRecipe.setInstructions(request.getInstructions());

        Recipe updatedRecipe = recipeRepository.save(existingRecipe);
        return recipeMapper.toResponseDTO(updatedRecipe);
    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy công thức với ID: " + id);
        }
        recipeRepository.deleteById(id);
    }
}
