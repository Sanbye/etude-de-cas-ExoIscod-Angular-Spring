package com.codeSolution.PMT.controller;

import com.codeSolution.PMT.dto.AuthResponse;
import com.codeSolution.PMT.dto.LoginRequest;
import com.codeSolution.PMT.dto.RegisterRequest;
import com.codeSolution.PMT.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        authResponse = new AuthResponse(null, null, null, null);
        authResponse.setUserId(userId);
        authResponse.setUsername("testuser");
        authResponse.setEmail("test@example.com");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse responseBody = (AuthResponse) response.getBody();
        assertEquals(userId, responseBody.getUserId());
        assertEquals("testuser", responseBody.getUsername());
        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_WhenServiceThrowsException() {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("User already exists"));

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse responseBody = (AuthResponse) response.getBody();
        assertEquals(userId, responseBody.getUserId());
        assertEquals("testuser", responseBody.getUsername());
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_WhenServiceThrowsException() {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
        verify(authService, times(1)).login(any(LoginRequest.class));
    }
}
