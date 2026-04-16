package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.LoginRequestDTO;
import com.quang.smart_recipe.dto.request.RegisterRequestDTO;
import com.quang.smart_recipe.dto.response.AuthResponseDTO;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.repository.UserRepository;
import com.quang.smart_recipe.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ĐĂNG KÝ
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER"); // Mặc định là User thường

        User savedUser = userRepository.save(user);

        // In thẻ từ luôn cho khách
        String jwtToken = jwtService.generateToken(savedUser);

        return new AuthResponseDTO(jwtToken, savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    // ĐĂNG NHẬP
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Hàm này tự động ném lỗi nếu sai Pass/User
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // Cấp thẻ từ mới
        String jwtToken = jwtService.generateToken(user);

        return new AuthResponseDTO(jwtToken, user.getId(), user.getUsername(), user.getRole());
    }
}