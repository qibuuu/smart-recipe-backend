package com.quang.smart_recipe.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FridgeItemResponseDTO {
    private Long id;
    private Long ingredientId;

    private String ingredientName;
    private String unit;

    private Float amount;
    private LocalDate expiryDate;
}
