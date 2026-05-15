package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.MealPlanRequestDTO;
import com.quang.smart_recipe.dto.response.MealPlanResponseDTO;
import com.quang.smart_recipe.service.MealPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping
    public ResponseEntity<List<MealPlanResponseDTO>> getMyMealPlans() {
        return ResponseEntity.ok(mealPlanService.getMyMealPlans());
    }

    @PostMapping
    public ResponseEntity<MealPlanResponseDTO> addMealPlan(@Valid @RequestBody MealPlanRequestDTO request) {
        return ResponseEntity.ok(mealPlanService.addMealPlan(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable Long id) {
        mealPlanService.deleteMealPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/export-to-cart")
    public ResponseEntity<Void> exportToCart() {
        mealPlanService.exportToCart();
        return ResponseEntity.ok().build();
    }
}
