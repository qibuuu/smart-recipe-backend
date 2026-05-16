package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.RecipeCreateRequestDTO;
import com.quang.smart_recipe.dto.request.RecipeIngredientRequestDTO;
import com.quang.smart_recipe.dto.request.RecipeRequestDTO;
import com.quang.smart_recipe.dto.response.RecipeResponseDTO;
import com.quang.smart_recipe.dto.response.RecipeSuggestionDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.entity.Recipe;
import com.quang.smart_recipe.entity.RecipeIngredient;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.mapper.RecipeMapper;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.repository.RecipeIngredientRepository;
import com.quang.smart_recipe.repository.RecipeRepository;
import com.quang.smart_recipe.repository.UserFridgeRepository;
import com.quang.smart_recipe.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserFridgeRepository userFridgeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;
    private final SecurityUtils securityUtils;

    @Value("${app.admin-id:1}")
    private Long adminId;

    @Value("${app.default-image:https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800}")
    private String defaultImage;

    // ── Public read ───────────────────────────────────────────

    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findByAuthorId(adminId)
                .stream()
                .map(recipeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RecipeResponseDTO> searchRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAllRecipes();
        return recipeRepository.findByAuthorIdAndTitleContainingIgnoreCase(adminId, keyword.trim())
                .stream()
                .map(recipeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public RecipeResponseDTO getRecipeById(Long id) {
        Recipe recipe = findRecipeOrThrow(id);
        RecipeResponseDTO dto = recipeMapper.toResponseDTO(recipe);

        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findByRecipeId(id);
        if (!recipeIngredients.isEmpty()) {
            List<String> ingList = recipeIngredients.stream()
                    .map(ri -> ri.getAmount() + " " + ri.getIngredient().getUnit() + " " + ri.getIngredient().getName())
                    .collect(Collectors.toList());
            dto.setIngredients(ingList);
        }
        return dto;
    }

    public List<RecipeResponseDTO> getMyRecipes() {
        return recipeRepository.findByAuthorId(securityUtils.getCurrentUserId())
                .stream()
                .map(recipeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Suggest ───────────────────────────────────────────────

    public List<RecipeSuggestionDTO> suggestRecipesByIngredients(List<String> inputIngredients) {
        List<String> normalizedInputs = inputIngredients.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // 1 query to get all recipe-ingredient links (replaces N+1)
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = recipeIngredientRepository
                .findAllWithIngredients()
                .stream()
                .collect(Collectors.groupingBy(ri -> ri.getRecipe().getId()));

        // 1 query to get all recipes
        return recipeRepository.findAll().stream()
                .map(recipe -> buildSuggestion(recipe, normalizedInputs, ingredientsByRecipe))
                .filter(dto -> dto != null && dto.getMatchPercentage() > 0)
                .sorted((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()))
                .collect(Collectors.toList());
    }

    // ── CRUD (Admin recipes) ──────────────────────────────────

    public RecipeResponseDTO createRecipe(RecipeRequestDTO request) {
        Recipe recipe = recipeMapper.toEntity(request);
        return recipeMapper.toResponseDTO(recipeRepository.save(recipe));
    }

    public RecipeResponseDTO updateRecipe(Long id, RecipeRequestDTO request) {
        Recipe recipe = findRecipeOrThrow(id);
        recipe.setTitle(request.getTitle());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setInstructions(request.getInstructions());
        return recipeMapper.toResponseDTO(recipeRepository.save(recipe));
    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        recipeRepository.deleteById(id);
    }

    // ── Custom recipes (User's Kitchen) ──────────────────────

    @Transactional
    public RecipeResponseDTO createCustomRecipe(RecipeCreateRequestDTO request) {
        User user = securityUtils.getCurrentUser();

        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCalories(request.getCalories());
        recipe.setServings(request.getServings());
        recipe.setImageUrl(request.getImageUrl() != null && !request.getImageUrl().isEmpty()
                ? request.getImageUrl() : defaultImage);
        recipe.setTags(request.getTags());
        recipe.setInstructions(request.getInstructions());
        recipe.setAuthorId(user.getId());

        Recipe saved = recipeRepository.save(recipe);
        saveIngredients(saved, request.getIngredients());
        return getRecipeById(saved.getId());
    }

    @Transactional
    public RecipeResponseDTO updateCustomRecipe(Long id, RecipeCreateRequestDTO request) {
        Recipe recipe = findRecipeOrThrow(id);
        assertOwnership(recipe);

        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCalories(request.getCalories());
        recipe.setServings(request.getServings());
        recipe.setTags(request.getTags());
        recipe.setInstructions(request.getInstructions());
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            recipe.setImageUrl(request.getImageUrl());
        }
        recipeRepository.save(recipe);

        recipeIngredientRepository.deleteByRecipeId(id);
        saveIngredients(recipe, request.getIngredients());
        return getRecipeById(id);
    }

    @Transactional
    public void deleteCustomRecipe(Long id) {
        Recipe recipe = findRecipeOrThrow(id);
        assertOwnership(recipe);
        recipeIngredientRepository.deleteByRecipeId(id);
        recipeRepository.delete(recipe);
    }

    // ── Private helpers ───────────────────────────────────────

    private Recipe findRecipeOrThrow(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND, "Không tìm thấy công thức ID: " + id));
    }

    private void assertOwnership(Recipe recipe) {
        if (!recipe.getAuthorId().equals(securityUtils.getCurrentUserId())) {
            throw new AppException(ErrorCode.RECIPE_ACCESS_DENIED);
        }
    }

    private void saveIngredients(Recipe recipe, List<RecipeIngredientRequestDTO> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return;

        List<RecipeIngredient> links = new ArrayList<>();
        for (RecipeIngredientRequestDTO req : ingredients) {
            String name = req.getIngredientName().trim();
            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(null, name, req.getUnit(), null)));

            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setIngredient(ingredient);
            ri.setAmount(req.getAmount());
            links.add(ri);
        }
        recipeIngredientRepository.saveAll(links);
    }

    private RecipeSuggestionDTO buildSuggestion(Recipe recipe, List<String> normalizedInputs,
                                                 Map<Long, List<RecipeIngredient>> ingredientsByRecipe) {
        List<RecipeIngredient> required = ingredientsByRecipe.getOrDefault(recipe.getId(), List.of());
        if (required.isEmpty()) return null;

        int matchCount = 0;
        List<String> missing = new ArrayList<>();

        for (RecipeIngredient req : required) {
            String requiredName = req.getIngredient().getName().toLowerCase();
            boolean found = normalizedInputs.stream()
                    .anyMatch(input -> requiredName.contains(input) || input.contains(requiredName));
            if (found) matchCount++;
            else missing.add(req.getIngredient().getName());
        }

        int matchPct = (matchCount * 100) / required.size();
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
