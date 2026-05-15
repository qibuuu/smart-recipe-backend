package com.quang.smart_recipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String role;
}
