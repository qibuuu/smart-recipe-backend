package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByDifficulty(String difficulty);
}
