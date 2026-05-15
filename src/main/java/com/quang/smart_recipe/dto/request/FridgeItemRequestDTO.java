package com.quang.smart_recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FridgeItemRequestDTO {

    // 1. Nhận tên nguyên liệu bằng chữ (Bắt buộc phải có)
    @NotBlank(message = "Tên nguyên liệu không được để trống")
    private String ingredientName;

    // 2. Số lượng (Bắt buộc phải có)
    @NotNull(message = "Số lượng không được để trống")
    private Float amount;

    // 3. Ngày hết hạn (KHÔNG BẮT BUỘC -> Không dùng @NotNull)
    private LocalDate expiryDate;

    // (TUYỆT ĐỐI KHÔNG CÓ userId Ở ĐÂY NỮA NHÉ)
}