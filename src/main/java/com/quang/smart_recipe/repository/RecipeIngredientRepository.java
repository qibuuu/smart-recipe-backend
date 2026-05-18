package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.RecipeIngredient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    List<RecipeIngredient> findByRecipeId(Long recipeId);

    /** Fetches ALL recipe-ingredient links with their ingredient data in ONE query.
     *  Used by suggestRecipesByIngredients to avoid N+1 problem. */
    @Query("SELECT ri FROM RecipeIngredient ri JOIN FETCH ri.ingredient JOIN FETCH ri.recipe")
    List<RecipeIngredient> findAllWithIngredients();

    @Query("SELECT DISTINCT ri.recipe.id FROM RecipeIngredient ri WHERE LOWER(ri.ingredient.name) IN :names")
    List<Long> findRecipeIdsByIngredientNames(@Param("names") java.util.Collection<String> names);

    @Query("SELECT ri FROM RecipeIngredient ri JOIN FETCH ri.ingredient JOIN FETCH ri.recipe WHERE ri.recipe.id IN :recipeIds")
    List<RecipeIngredient> findByRecipeIdIn(@Param("recipeIds") java.util.Collection<Long> recipeIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecipeIngredient ri WHERE ri.recipe.id = :recipeId")
    void deleteByRecipeId(Long recipeId);
}
