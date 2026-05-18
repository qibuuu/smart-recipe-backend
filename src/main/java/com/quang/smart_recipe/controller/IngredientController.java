package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.IngredientRequestDTO;
import com.quang.smart_recipe.dto.response.IngredientResponseDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping
    public List<IngredientResponseDTO> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping
    public IngredientResponseDTO createIngredient(@Valid @RequestBody IngredientRequestDTO request) {
        return ingredientService.createIngredient(request);
    }

    @PutMapping("/{id}")
    public IngredientResponseDTO updateIngredient(@PathVariable Long id, @Valid @RequestBody IngredientRequestDTO request) {
        return ingredientService.updateIngredient(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(Map.of("message", "Xóa nguyên liệu thành công"));
    }
}

