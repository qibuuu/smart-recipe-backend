package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCase(String name);

    Optional<Ingredient> findByName(String name);
    Optional<Ingredient> findByNameIgnoreCase(String name);
    List<Ingredient> findByNameInIgnoreCase(java.util.Collection<String> names);
}
