package com.quang.smart_recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IngredientRequestDTO {
    @NotBlank(message = "Tên nguyên liệu không được để trống!")
    private String name;

    @NotBlank(message = "Đơn vị tính không được để trống!")
    private String unit;

    private String imageUrl;
}
