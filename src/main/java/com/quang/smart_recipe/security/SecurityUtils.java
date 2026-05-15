package com.quang.smart_recipe.security;

import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility bean để lấy thông tin User đang đăng nhập từ Spring Security context.
 * Inject vào bất kỳ Service nào cần — tránh copy-paste getCurrentUser() khắp nơi.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
