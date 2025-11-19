package com.codeSolution.PNT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // Peut être null si pas de JWT
    private Long userId;
    private String username;
    private String email;
}

