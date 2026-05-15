package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.RecipeCreateRequestDTO;
import com.quang.smart_recipe.dto.request.RecipeRequestDTO;
import com.quang.smart_recipe.dto.response.RecipeResponseDTO;
import com.quang.smart_recipe.dto.response.RecipeSuggestionDTO;
import com.quang.smart_recipe.service.FileService;
import com.quang.smart_recipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes(@RequestParam(required = false) String keyword) {
        List<RecipeResponseDTO> result = (keyword != null && !keyword.trim().isEmpty())
                ? recipeService.searchRecipes(keyword)
                : recipeService.getAllRecipes();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<RecipeSuggestionDTO>> suggestRecipes(@RequestParam List<String> ingredients) {
        return ResponseEntity.ok(recipeService.suggestRecipesByIngredients(ingredients));
    }

    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getMyRecipes() {
        return ResponseEntity.ok(recipeService.getMyRecipes());
    }

    @PostMapping
    public ResponseEntity<RecipeResponseDTO> createRecipe(@Valid @RequestBody RecipeRequestDTO request) {
        return ResponseEntity.ok(recipeService.createRecipe(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeRequestDTO request) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/my-kitchen")
    public ResponseEntity<RecipeResponseDTO> createCustomRecipe(@RequestBody RecipeCreateRequestDTO request) {
        return ResponseEntity.ok(recipeService.createCustomRecipe(request));
    }

    @PutMapping("/my-kitchen/{id}")
    public ResponseEntity<RecipeResponseDTO> updateCustomRecipe(@PathVariable Long id, @RequestBody RecipeCreateRequestDTO request) {
        return ResponseEntity.ok(recipeService.updateCustomRecipe(id, request));
    }

    @DeleteMapping("/my-kitchen/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomRecipe(@PathVariable Long id) {
        recipeService.deleteCustomRecipe(id);
        return ResponseEntity.ok(Map.of("message", "Xóa món ăn thành công"));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
