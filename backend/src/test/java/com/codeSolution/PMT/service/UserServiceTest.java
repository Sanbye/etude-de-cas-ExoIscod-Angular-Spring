package com.codeSolution.PMT.service;

import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testFindAll() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenUserExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(testUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void testFindById_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(testUserId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void testFindByUserName_WhenUserExists() {
        // Given
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUserName("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    void testFindByEmail_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testSave() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.save(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeleteById() {
        // When
        userService.deleteById(testUserId);

        // Then
        verify(userRepository, times(1)).deleteById(testUserId);
    }

    @Test
    void testExistsByUserName_WhenExists() {
        // Given
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        // When
        boolean result = userService.existsByUserName("testuser");

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByUserName("testuser");
    }

    @Test
    void testExistsByUserName_WhenDoesNotExist() {
        // Given
        when(userRepository.existsByUserName("testuser")).thenReturn(false);

        // When
        boolean result = userService.existsByUserName("testuser");

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsByUserName("testuser");
    }

    @Test
    void testExistsByEmail_WhenExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void testExistsByEmail_WhenDoesNotExist() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }
}

