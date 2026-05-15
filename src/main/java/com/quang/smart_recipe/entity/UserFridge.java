package com.quang.smart_recipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_fridge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "Ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Float amount;

    private LocalDate expiryDate;
}
