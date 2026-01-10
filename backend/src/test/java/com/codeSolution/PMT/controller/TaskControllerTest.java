package com.codeSolution.PMT.controller;

import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
import com.codeSolution.PMT.service.TaskService;
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
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task testTask;
    private UUID taskId;
    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testTask = new Task();
        testTask.setId(taskId);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setPriority(Task.TaskPriority.MEDIUM);
    }

    @Test
    void testGetAllTasks() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.findAll()).thenReturn(tasks);

        // When
        ResponseEntity<List<Task>> response = taskController.getAllTasks();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testTask, response.getBody().get(0));
        verify(taskService, times(1)).findAll();
    }

    @Test
    void testGetTaskById_WhenTaskExists() {
        // Given
        when(taskService.findById(taskId)).thenReturn(Optional.of(testTask));

        // When
        ResponseEntity<Task> response = taskController.getTaskById(taskId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTask, response.getBody());
        verify(taskService, times(1)).findById(taskId);
    }

    @Test
    void testGetTaskById_WhenTaskDoesNotExist() {
        // Given
        when(taskService.findById(taskId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Task> response = taskController.getTaskById(taskId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).findById(taskId);
    }

    @Test
    void testGetTasksByProject() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.findByProjectId(projectId)).thenReturn(tasks);

        // When
        ResponseEntity<List<Task>> response = taskController.getTasksByProject(projectId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService, times(1)).findByProjectId(projectId);
    }

    @Test
    void testGetTasksByProjectAndStatus_ValidStatus() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.findByProjectIdAndStatus(projectId, Task.TaskStatus.TODO)).thenReturn(tasks);

        // When
        ResponseEntity<List<Task>> response = taskController.getTasksByProjectAndStatus(projectId, "TODO");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService, times(1)).findByProjectIdAndStatus(projectId, Task.TaskStatus.TODO);
    }

    @Test
    void testGetTasksByProjectAndStatus_InvalidStatus() {
        // When
        ResponseEntity<List<Task>> response = taskController.getTasksByProjectAndStatus(projectId, "INVALID");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taskService, never()).findByProjectIdAndStatus(any(), any());
    }

    @Test
    void testGetTasksByAssignedUser() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.findByAssignedUserId(userId)).thenReturn(tasks);

        // When
        ResponseEntity<List<Task>> response = taskController.getTasksByAssignedUser(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService, times(1)).findByAssignedUserId(userId);
    }

    @Test
    void testGetTaskHistory() {
        // Given
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        List<TaskHistory> histories = Arrays.asList(history);
        when(taskService.getTaskHistory(taskId)).thenReturn(histories);

        // When
        ResponseEntity<List<TaskHistory>> response = taskController.getTaskHistory(taskId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService, times(1)).getTaskHistory(taskId);
    }

    @Test
    void testCreateTask() {
        // Given
        when(taskService.save(any(Task.class), any(UUID.class), any(UUID.class))).thenReturn(testTask);

        // When
        ResponseEntity<Task> response = taskController.createTask(testTask);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTask, response.getBody());
        verify(taskService, times(1)).save(any(Task.class), any(UUID.class), any(UUID.class));
    }

    @Test
    void testUpdateTask_WhenTaskExists() {
        // Given
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Updated Task");
        when(taskService.updateTask(any(UUID.class), any(Task.class), any(UUID.class), any(UUID.class)))
                .thenReturn(updatedTask);

        // When
        ResponseEntity<Task> response = taskController.updateTask(taskId, updatedTask);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Task", response.getBody().getName());
        verify(taskService, times(1)).updateTask(any(UUID.class), any(Task.class), any(UUID.class), any(UUID.class));
    }

    @Test
    void testUpdateTask_WhenTaskDoesNotExist() {
        // Given
        when(taskService.updateTask(any(UUID.class), any(Task.class), any(UUID.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Task not found"));

        // When
        ResponseEntity<Task> response = taskController.updateTask(taskId, testTask);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).updateTask(any(UUID.class), any(Task.class), any(UUID.class), any(UUID.class));
    }

    @Test
    void testDeleteTask_WhenTaskExists() {
        // Given
        when(taskService.findById(taskId)).thenReturn(Optional.of(testTask));
        doNothing().when(taskService).deleteById(taskId);

        // When
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).findById(taskId);
        verify(taskService, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteTask_WhenTaskDoesNotExist() {
        // Given
        when(taskService.findById(taskId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService, times(1)).findById(taskId);
        verify(taskService, never()).deleteById(taskId);
    }

    @Test
    void testAssignTask_Success() {
        // Given
        UUID projectMemberProjectId = UUID.randomUUID();
        UUID projectMemberUserId = UUID.randomUUID();
        when(taskService.assignToProjectMember(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class)))
                .thenReturn(testTask);

        // When
        ResponseEntity<Task> response = taskController.assignTask(taskId, projectMemberProjectId, projectMemberUserId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(taskService, times(1)).assignToProjectMember(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class));
    }

    @Test
    void testAssignTask_WhenServiceThrowsException() {
        // Given
        UUID projectMemberProjectId = UUID.randomUUID();
        UUID projectMemberUserId = UUID.randomUUID();
        when(taskService.assignToProjectMember(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Task not found"));

        // When
        ResponseEntity<Task> response = taskController.assignTask(taskId, projectMemberProjectId, projectMemberUserId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService, times(1)).assignToProjectMember(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class));
    }
}
