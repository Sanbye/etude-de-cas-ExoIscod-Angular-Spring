package com.codeSolution.PNT.service;

import com.codeSolution.PNT.dto.AuthResponse;
import com.codeSolution.PNT.dto.LoginRequest;
import com.codeSolution.PNT.dto.RegisterRequest;
import com.codeSolution.PNT.model.User;
import com.codeSolution.PNT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Hashage du mot de passe avec BCrypt
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        // firstName et lastName sont optionnels

        User savedUser = userRepository.save(user);
        // Retourner une réponse simple sans token JWT
        return new AuthResponse(null, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Vérification du mot de passe avec BCrypt
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Retourner une réponse simple sans token JWT
        return new AuthResponse(null, user.getId(), user.getUsername(), user.getEmail());
    }
}

