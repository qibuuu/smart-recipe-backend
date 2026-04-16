package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFridgeRepository extends JpaRepository<UserFridge, Long> {
    List<UserFridge> findByUserId(Long userId);
}
