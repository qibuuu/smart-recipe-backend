package com.quang.smart_recipe.service;

import com.quang.smart_recipe.entity.UserFavorite;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.repository.UserFavoriteRepository;
import com.quang.smart_recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;

    // Hàm lấy User hiện tại
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    // Logic Thả tim / Bỏ tim
    public String toggleFavorite(Long recipeId) {
        User user = getCurrentUser();
        Optional<UserFavorite> existingFavorite = userFavoriteRepository.findByUserIdAndRecipeId(user.getId(), recipeId);

        if (existingFavorite.isPresent()) {
            userFavoriteRepository.delete(existingFavorite.get());
            return "Đã bỏ lưu món ăn";
        } else {
            UserFavorite newFavorite = UserFavorite.builder()
                    .userId(user.getId())
                    .recipeId(recipeId)
                    .build();
            userFavoriteRepository.save(newFavorite);
            return "Đã lưu món ăn thành công";
        }
    }

    // Logic Lấy danh sách ID
    public List<Long> getMyFavoriteRecipeIds() {
        User user = getCurrentUser();
        return userFavoriteRepository.findAllByUserId(user.getId())
                .stream()
                .map(UserFavorite::getRecipeId)
                .toList();
    }
}