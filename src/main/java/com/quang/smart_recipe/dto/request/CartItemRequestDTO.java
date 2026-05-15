package com.quang.smart_recipe.dto.request;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private String ingredientName;
    private Float amount;
    private String unit;
    private String note;
}