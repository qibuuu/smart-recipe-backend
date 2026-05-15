package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.CartItemRequestDTO;
import com.quang.smart_recipe.entity.ShoppingItem;
import com.quang.smart_recipe.service.ShoppingItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingItemController {

    private final ShoppingItemService shoppingItemService;

    // Lấy toàn bộ giỏ hàng
    @GetMapping
    public ResponseEntity<List<ShoppingItem>> getMyCart() {
        return ResponseEntity.ok(shoppingItemService.getMyCart());
    }

    // Thêm đồ vào giỏ
    // SỬA LẠI API THÊM ĐỒ VÀO GIỎ
    @PostMapping
    public ResponseEntity<ShoppingItem> addItem(@RequestBody CartItemRequestDTO request) {
        return ResponseEntity.ok(shoppingItemService.addItem(request));
    }

    // Tick chọn / Bỏ tick
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ShoppingItem> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingItemService.toggleBoughtStatus(id));
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        shoppingItemService.deleteItem(id);
        return ResponseEntity.ok("Đã xóa");
    }
}