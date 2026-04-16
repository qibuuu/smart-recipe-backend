package com.quang.smart_recipe.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FridgeItemRequestDTO {
    @NotNull(message = "Id người dùng không được để trống")
    private Long userId;

    @NotNull(message = "Id nguyên liệu không được để trống")
    private Long ingredientId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn 0")
    private Float amount;

    @Future(message = "Ngày hết hạn không hợp lệ")
    private LocalDate expiryDate;
}
