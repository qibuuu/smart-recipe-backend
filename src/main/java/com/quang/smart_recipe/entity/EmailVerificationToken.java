package com.quang.smart_recipe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token; // 6-digit OTP

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private String rawPassword; // Temporary storage for welcome email

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
