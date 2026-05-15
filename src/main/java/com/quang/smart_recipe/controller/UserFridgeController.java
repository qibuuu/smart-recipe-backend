package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.dto.request.FridgeItemRequestDTO;
import com.quang.smart_recipe.dto.response.FridgeItemResponseDTO;
import com.quang.smart_recipe.service.UserFridgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fridge")
@RequiredArgsConstructor
public class UserFridgeController {

    private final UserFridgeService userFridgeService;

    // Đổi thành @GetMapping thôi, không cần truyền {userId} nữa
    @GetMapping
    public List<FridgeItemResponseDTO> getMyFridge() {
        return userFridgeService.getMyFridge();
    }

    @PostMapping
    public FridgeItemResponseDTO addItemToFridge(@Valid @RequestBody FridgeItemRequestDTO request) {
        return userFridgeService.addItemToFridge(request);
    }

    @DeleteMapping("/{fridgeItemId}")
    public void deleteItem(@PathVariable Long fridgeItemId) {
        userFridgeService.deleteItem(fridgeItemId);
    }

    // Bổ sung API này vào cuối class
    @GetMapping("/suggest")
    public ResponseEntity<?> suggestRecipes() {
        return ResponseEntity.ok(userFridgeService.suggestRecipes());
    }
}