package com.quang.smart_recipe.controller;


import com.quang.smart_recipe.dto.response.RecipeSuggestionDTO;
import com.quang.smart_recipe.dto.request.RecipeRequestDTO;
import com.quang.smart_recipe.dto.response.RecipeResponseDTO;
import com.quang.smart_recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/suggest")
    public List<RecipeSuggestionDTO> suggestRecipes(@RequestParam Long userId) {
        return recipeService.suggestRecipesForUser(userId);
    }

    @PostMapping
    public RecipeResponseDTO createRecipe(@Valid @RequestBody RecipeRequestDTO request) {
        return recipeService.createRecipe(request);
    }

    @PutMapping("/{id}")
    public RecipeResponseDTO updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeRequestDTO request) {
        return recipeService.updateRecipe(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }
}
