package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.FridgeItemRequestDTO;
import com.quang.smart_recipe.dto.response.FridgeItemResponseDTO;
import com.quang.smart_recipe.service.UserFridgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fridge")
@RequiredArgsConstructor
public class UserFridgeController {
    private final UserFridgeService userFridgeService;

    @GetMapping("/{userId}")
    public List<FridgeItemResponseDTO> getMyFridge(@PathVariable Long userId) {
        return userFridgeService.getMyFridge(userId);
    }

    @PostMapping
    public FridgeItemResponseDTO addItemToFridge(@Valid @RequestBody FridgeItemRequestDTO request) {
        return userFridgeService.addItemToFridge(request);
    }

    @DeleteMapping("/{fridgeItemId}")
    public void deleteItem(@PathVariable Long fridgeItemId) {
        userFridgeService.deleteItem(fridgeItemId);
    }
}
