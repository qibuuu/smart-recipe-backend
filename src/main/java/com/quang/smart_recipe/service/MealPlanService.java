package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.MealPlanRequestDTO;
import com.quang.smart_recipe.dto.response.MealPlanResponseDTO;
import com.quang.smart_recipe.entity.*;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.repository.*;
import com.quang.smart_recipe.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<MealPlanResponseDTO> getMyMealPlans() {
        Long userId = securityUtils.getCurrentUserId();
        return mealPlanRepository.findByUserId(userId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MealPlanResponseDTO addMealPlan(MealPlanRequestDTO request) {
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        MealPlan mealPlan = MealPlan.builder()
                .user(user)
                .recipe(recipe)
                .dayOfWeek(request.getDayOfWeek())
                .mealType(request.getMealType())
                .build();

        return convertToResponseDTO(mealPlanRepository.save(mealPlan));
    }

    @Transactional
    public void deleteMealPlan(Long id) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEAL_PLAN_NOT_FOUND));
        
        if (!mealPlan.getUser().getId().equals(securityUtils.getCurrentUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        mealPlanRepository.delete(mealPlan);
    }

    @Transactional
    public void exportToCart() {
        Long userId = securityUtils.getCurrentUserId();
        List<MealPlan> plans = mealPlanRepository.findByUserId(userId);

        for (MealPlan plan : plans) {
            Recipe recipe = plan.getRecipe();
            if (recipe.getRecipeIngredients() != null) {
                for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                    Ingredient ing = ri.getIngredient();
                    
                    // Additive logic: check if already in cart
                    // For simplicity, we just add it as a new item as requested "cộng dồn"
                    ShoppingItem item = new ShoppingItem();
                    item.setUserId(userId);
                    item.setIngredientName(ing.getName());
                    item.setAmount(ri.getAmount());
                    item.setUnit(ing.getUnit());
                    item.setBought(false);
                    item.setNote("Từ thực đơn: " + recipe.getTitle());
                    
                    shoppingItemRepository.save(item);
                }
            }
        }
    }

    private MealPlanResponseDTO convertToResponseDTO(MealPlan mealPlan) {
        return MealPlanResponseDTO.builder()
                .id(mealPlan.getId())
                .recipeId(mealPlan.getRecipe().getId())
                .recipeTitle(mealPlan.getRecipe().getTitle())
                .recipeImageUrl(mealPlan.getRecipe().getImageUrl())
                .dayOfWeek(mealPlan.getDayOfWeek())
                .mealType(mealPlan.getMealType())
                .build();
    }
}
