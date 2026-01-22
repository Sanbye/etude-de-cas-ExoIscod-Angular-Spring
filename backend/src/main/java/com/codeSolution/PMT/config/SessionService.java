package com.codeSolution.PMT.config;

import com.codeSolution.PMT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final UserRepository userRepository;
    
    public boolean isValidUser(@NonNull UUID userId) {
        return userRepository.findById(userId).isPresent();
    }
}
