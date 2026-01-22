package com.codeSolution.PMT.security;

import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration pour vérifier le comportement du filtre d'authentification
 * via des requêtes HTTP réelles
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UUID validUserId;
    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
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
    void testFilter_WithValidUserId_ShouldSetAuthentication() throws Exception {
        // When & Then - La requête devrait réussir car l'utilisateur existe
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", validUserId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testFilter_WithInvalidUserId_ShouldNotSetAuthentication() throws Exception {
        // When & Then - La requête devrait échouer car l'utilisateur n'existe pas
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", invalidUserId.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFilter_WithoutHeader_ShouldNotSetAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFilter_WithInvalidUUIDFormat_ShouldNotSetAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", "not-a-valid-uuid"))
                .andExpect(status().isUnauthorized());
    }
}
