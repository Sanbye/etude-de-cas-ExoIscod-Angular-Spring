package com.codeSolution.PMT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // Peut être null si pas de JWT
    private UUID userId;
    private String username;
    private String email;
}

