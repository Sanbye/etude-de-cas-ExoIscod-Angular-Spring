package com.codeSolution.PMT.repository;

import com.codeSolution.PMT.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testSave() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUserName());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void testFindById() {
        // Given
        User savedUser = userRepository.save(testUser);
        UUID userId = savedUser.getId();

        // When
        Optional<User> foundUser = userRepository.findById(userId);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("testuser", foundUser.get().getUserName());
    }

    @Test
    void testFindByUserName() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findByUserName("testuser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUserName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByUserName_WhenNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByUserName("nonexistent");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByEmail() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("testuser", foundUser.get().getUserName());
    }

    @Test
    void testFindByEmail_WhenNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByUserName_WhenExists() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByUserName("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByUserName_WhenDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUserName("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_WhenExists() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_WhenDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testDelete() {
        // Given
        User savedUser = userRepository.save(testUser);
        UUID userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);

        // Then
        Optional<User> foundUser = userRepository.findById(userId);
        assertFalse(foundUser.isPresent());
    }
}

