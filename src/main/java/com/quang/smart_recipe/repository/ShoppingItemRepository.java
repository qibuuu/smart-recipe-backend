package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    // Lấy toàn bộ danh sách đồ cần mua của 1 user
    List<ShoppingItem> findAllByUserId(Long userId);
}