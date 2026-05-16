package com.quang.smart_recipe.repository;

import com.quang.smart_recipe.entity.EmailVerificationToken;
import com.quang.smart_recipe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(User user);

    @Transactional
    void deleteByUser(User user);
}
