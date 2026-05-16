package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserFridgeRepository extends JpaRepository<UserFridge, Long> {
    List<UserFridge> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFridge uf WHERE uf.user.id = :userId")
    void deleteByUserId(Long userId);
}
