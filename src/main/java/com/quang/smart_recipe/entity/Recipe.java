package com.quang.smart_recipe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name = "recipes")
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;

    private String difficulty;
    private Integer prepTime;
    private Integer calories;
    private Integer servings;
    private String imageUrl;

    @Column(name = "author_id")
    private Long authorId;

    private String tags;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<RecipeIngredient> recipeIngredients;
}
