package com.quang.smart_recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // Thêm: implements UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String role; // "USER" hoặc "ADMIN"

    // --- CÁC HÀM BẮT BUỘC CỦA SPRING SECURITY ---

    // Cấp quyền cho user (Dựa vào biến role)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    // Spring Security dùng hàm này để lấy password kiểm tra
    @Override
    public String getPassword() {
        return password;
    }

    // Spring Security dùng hàm này để lấy username
    @Override
    public String getUsername() {
        return username;
    }

    // Tài khoản có bị hết hạn không? (Trả về true = không bị)
    @Override
    public boolean isAccountNonExpired() { return true; }

    // Tài khoản có bị khóa không?
    @Override
    public boolean isAccountNonLocked() { return true; }

    // Thông tin đăng nhập có bị hết hạn không?
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // Tài khoản có đang kích hoạt không?
    @Override
    public boolean isEnabled() { return true; }
}