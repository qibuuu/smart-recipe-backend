package com.quang.smart_recipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private boolean emailVerified;
}
