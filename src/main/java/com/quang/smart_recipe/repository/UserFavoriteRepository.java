package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    // Lấy tất cả món ăn yêu thích của một user
    List<UserFavorite> findAllByUserId(Long userId);

    // Kiểm tra xem user đã lưu món này chưa
    Optional<UserFavorite> findByUserIdAndRecipeId(Long userId, Long recipeId);
}