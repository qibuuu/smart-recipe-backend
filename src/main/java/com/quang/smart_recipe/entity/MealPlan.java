package com.quang.smart_recipe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // 'Monday', 'Tuesday', etc. or 'Thứ 2', 'Thứ 3'

    @Column(name = "meal_type", nullable = false)
    private String mealType; // 'Breakfast', 'Lunch', 'Dinner'
}
