package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.FridgeItemRequestDTO;
import com.quang.smart_recipe.dto.response.FridgeItemResponseDTO;
import com.quang.smart_recipe.dto.response.RecipeSuggestionDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.entity.Recipe;
import com.quang.smart_recipe.entity.RecipeIngredient;
import com.quang.smart_recipe.entity.UserFridge;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.mapper.UserFridgeMapper;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.repository.RecipeIngredientRepository;
import com.quang.smart_recipe.repository.RecipeRepository;
import com.quang.smart_recipe.repository.UserFridgeRepository;
import com.quang.smart_recipe.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFridgeService {

    private final UserFridgeRepository userFridgeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserFridgeMapper fridgeMapper;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final SecurityUtils securityUtils;

    public FridgeItemResponseDTO addItemToFridge(FridgeItemRequestDTO request) {
        Ingredient ingredient = ingredientRepository.findByName(request.getIngredientName())
                .orElseGet(() -> {
                    Ingredient newIng = new Ingredient();
                    newIng.setName(request.getIngredientName());
                    return ingredientRepository.save(newIng);
                });

        UserFridge item = new UserFridge();
        item.setUser(securityUtils.getCurrentUser());
        item.setIngredient(ingredient);
        item.setAmount(request.getAmount());
        item.setExpiryDate(request.getExpiryDate());

        return fridgeMapper.toResponseDTO(userFridgeRepository.save(item));
    }

    public List<FridgeItemResponseDTO> getMyFridge() {
        return userFridgeRepository.findByUserId(securityUtils.getCurrentUserId())
                .stream()
                .map(fridgeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long id) {
        if (!userFridgeRepository.existsById(id)) throw new AppException(ErrorCode.FRIDGE_ITEM_NOT_FOUND);
        userFridgeRepository.deleteById(id);
    }

    public List<RecipeSuggestionDTO> suggestRecipes() {
        List<UserFridge> fridgeItems = userFridgeRepository.findByUserId(securityUtils.getCurrentUserId());
        if (fridgeItems.isEmpty()) return List.of();

        Set<Long> myIngredientIds = fridgeItems.stream()
                .map(item -> item.getIngredient().getId())
                .collect(Collectors.toSet());

        return recipeRepository.findAll().stream()
                .map(recipe -> buildSuggestion(recipe, myIngredientIds))
                .filter(dto -> dto != null && dto.getMatchPercentage() > 0)
                .sorted((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()))
                .limit(6)
                .collect(Collectors.toList());
    }

    private RecipeSuggestionDTO buildSuggestion(Recipe recipe, Set<Long> myIngredientIds) {
        List<RecipeIngredient> required = recipeIngredientRepository.findByRecipeId(recipe.getId());
        if (required == null || required.isEmpty()) return null;

        int matchCount = 0;
        List<String> missing = new ArrayList<>();

        for (RecipeIngredient req : required) {
            if (myIngredientIds.contains(req.getIngredient().getId())) {
                matchCount++;
            } else {
                missing.add(req.getIngredient().getName());
            }
        }

        int matchPct = (int) Math.round((double) matchCount / required.size() * 100);
        if (matchPct == 0) return null;

        RecipeSuggestionDTO dto = new RecipeSuggestionDTO();
        dto.setRecipeId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setInstructions(recipe.getInstructions());
        dto.setMatchPercentage(matchPct);
        dto.setMissingIngredients(missing);
        return dto;
    }
}