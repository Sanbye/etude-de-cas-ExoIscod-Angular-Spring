package com.codeSolution.PMT.security;

import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UUID validUserId;
    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Créer un utilisateur de test
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser = userRepository.save(testUser);
        validUserId = testUser.getId();

        // Créer un UUID invalide (n'existe pas en base)
        invalidUserId = UUID.randomUUID();
    }

    // ========== Tests pour les routes publiques (/api/auth/**) ==========

    @Test
    void testPublicRoute_Register_ShouldBeAccessible() throws Exception {
        String registerRequest = """
            {
                "username": "newuser",
                "email": "newuser@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void testPublicRoute_Login_ShouldBeAccessible() throws Exception {
        // Créer un utilisateur avec mot de passe hashé pour le test
        User loginUser = new User();
        loginUser.setUserName("loginuser");
        loginUser.setEmail("login@example.com");
        loginUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password123
        userRepository.save(loginUser);

        String loginRequest = """
            {
                "email": "login@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value("loginuser"));
    }

    // ========== Tests pour les routes protégées ==========

    @Test
    void testProtectedRoute_GetProjects_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_GetProjects_WithInvalidUserId_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", invalidUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_GetProjects_WithValidUserId_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", validUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedRoute_GetUsers_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_GetUsers_WithValidUserId_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("X-User-Id", validUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedRoute_GetTasks_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_GetTasks_WithValidUserId_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("X-User-Id", validUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedRoute_CreateProject_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        String projectRequest = """
            {
                "name": "New Project",
                "description": "Test Description"
            }
            """;

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_CreateProject_WithValidUserId_ShouldReturnCreated() throws Exception {
        String projectRequest = """
            {
                "name": "New Project",
                "description": "Test Description"
            }
            """;

        mockMvc.perform(post("/api/projects")
                .header("X-User-Id", validUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void testProtectedRoute_UpdateProject_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        String projectRequest = """
            {
                "name": "Updated Project",
                "description": "Updated Description"
            }
            """;

        UUID projectId = UUID.randomUUID();
        mockMvc.perform(put("/api/projects/" + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_DeleteProject_WithoutHeader_ShouldReturnUnauthorized() throws Exception {
        UUID projectId = UUID.randomUUID();
        mockMvc.perform(delete("/api/projects/" + projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ========== Tests pour les headers invalides ==========

    @Test
    void testProtectedRoute_WithInvalidUUIDFormat_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", "not-a-valid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_WithEmptyHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedRoute_WithNullHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", (String) null)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ========== Tests pour vérifier que l'utilisateur est bien dans le contexte Spring Security ==========

    @Test
    void testProtectedRoute_WithValidUserId_ShouldSetAuthentication() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-User-Id", validUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // Si on arrive ici, c'est que l'authentification a fonctionné
        // car Spring Security aurait retourné 401 sinon
    }

    // ========== Tests pour les méthodes HTTP différentes ==========

    @Test
    void testProtectedRoute_OptionsRequest_ShouldBeAllowed() throws Exception {
        // Les requêtes OPTIONS sont généralement autorisées pour CORS
        mockMvc.perform(options("/api/projects")
                .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk());
    }
}
