package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.response.UserResponseDTO;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(Long id, UserResponseDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail().toLowerCase());
        if (request.getRole() != null) user.setRole(request.getRole());
        user.setEmailVerified(request.isEmailVerified());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}
