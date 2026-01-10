package com.codeSolution.PMT.controller;

import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testGetAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser, response.getBody().get(0));
        verify(userService, times(1)).findAll();
    }

    @Test
    void testGetUserById_WhenUserExists() {
        // Given
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<User> response = userController.getUserById(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_WhenUserDoesNotExist() {
        // Given
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.getUserById(userId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        when(userService.existsByUserName(testUser.getUserName())).thenReturn(false);
        when(userService.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.createUser(testUser);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).existsByUserName(testUser.getUserName());
        verify(userService, times(1)).existsByEmail(testUser.getEmail());
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_WhenUserNameExists() {
        // Given
        when(userService.existsByUserName(testUser.getUserName())).thenReturn(true);

        // When
        ResponseEntity<User> response = userController.createUser(testUser);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).existsByUserName(testUser.getUserName());
        verify(userService, never()).existsByEmail(anyString());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_WhenEmailExists() {
        // Given
        when(userService.existsByUserName(testUser.getUserName())).thenReturn(false);
        when(userService.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When
        ResponseEntity<User> response = userController.createUser(testUser);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).existsByUserName(testUser.getUserName());
        verify(userService, times(1)).existsByEmail(testUser.getEmail());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_WhenUserExists() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("updateduser");
        updatedUser.setEmail("updated@example.com");
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));
        when(userService.save(any(User.class))).thenReturn(updatedUser);

        // When
        ResponseEntity<User> response = userController.updateUser(userId, updatedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUserName());
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_WhenUserDoesNotExist() {
        // Given
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.updateUser(userId, testUser);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).findById(userId);
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_WhenUserExists() {
        // Given
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userService).deleteById(userId);

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_WhenUserDoesNotExist() {
        // Given
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findById(userId);
        verify(userService, never()).deleteById(userId);
    }
}
