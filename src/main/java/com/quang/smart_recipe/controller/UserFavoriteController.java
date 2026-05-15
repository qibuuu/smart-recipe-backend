package com.quang.smart_recipe.controller;

import com.quang.smart_recipe.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long recipeId) {
        // Chỉ gọi Service và trả về kết quả
        String message = userFavoriteService.toggleFavorite(recipeId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<Long>> getMyFavorites() {
        // Chỉ gọi Service và trả về kết quả
        List<Long> favoriteIds = userFavoriteService.getMyFavoriteRecipeIds();
        return ResponseEntity.ok(favoriteIds);
    }
}