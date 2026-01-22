package com.codeSolution.PMT.security;

import com.codeSolution.PMT.config.SessionService;
import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SessionServiceTest {

    @Autowired
    private UserRepository userRepository;

    private SessionService sessionService;
    private User testUser;
    private UUID validUserId;
    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(userRepository);
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
        validUserId = testUser.getId();

        invalidUserId = UUID.randomUUID();
    }

    @Test
    void testIsValidUser_WithValidUserId_ShouldReturnTrue() {
        // When
        boolean isValid = sessionService.isValidUser(validUserId);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsValidUser_WithInvalidUserId_ShouldReturnFalse() {
        // When
        boolean isValid = sessionService.isValidUser(invalidUserId);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testIsValidUser_AfterUserDeleted_ShouldReturnFalse() {
        // Given
        UUID userId = testUser.getId();
        userRepository.delete(testUser);
        userRepository.flush();

        // When
        boolean isValid = sessionService.isValidUser(userId);

        // Then
        assertFalse(isValid);
    }
}
