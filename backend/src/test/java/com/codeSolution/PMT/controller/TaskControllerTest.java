package com.codeSolution.PMT.controller;

import com.codeSolution.PMT.dto.AssignTaskRequest;
import com.codeSolution.PMT.dto.CreateTaskRequest;
import com.codeSolution.PMT.dto.TaskDTO;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
import com.codeSolution.PMT.service.TaskService;
import com.codeSolution.PMT.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task testTask;
    private ProjectMember testProjectMember;
    private UUID taskId;
    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testProjectMember = new ProjectMember();
        testProjectMember.setProjectId(projectId);
        testProjectMember.setUserId(userId);
        testProjectMember.setRole(Role.MEMBER);

        testTask = new Task();
        testTask.setId(taskId);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setPriority(Task.TaskPriority.MEDIUM);
        testTask.setProjectMember(testProjectMember);
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
    void testGetTaskById_Success() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(taskService.findByIdWithPermission(taskId, userId)).thenReturn(testTask);

            ResponseEntity<?> response = taskController.getTaskById(taskId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testTask, response.getBody());
            verify(taskService, times(1)).findByIdWithPermission(taskId, userId);
        }
    }

    @Test
    void testGetTaskById_Unauthorized() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            ResponseEntity<?> response = taskController.getTaskById(taskId);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).findByIdWithPermission(any(), any());
        }
    }

    @Test
    void testGetTaskById_WhenTaskDoesNotExist() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(taskService.findByIdWithPermission(taskId, userId))
                    .thenThrow(new RuntimeException("Task not found"));

            ResponseEntity<?> response = taskController.getTaskById(taskId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Task not found", response.getBody());
            verify(taskService, times(1)).findByIdWithPermission(taskId, userId);
        }
    }

    @Test
    void testGetTaskById_Forbidden() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(taskService.findByIdWithPermission(taskId, userId))
                    .thenThrow(new RuntimeException("You must be a member of the project to view task details."));

            ResponseEntity<?> response = taskController.getTaskById(taskId);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("You must be a member of the project to view task details.", response.getBody());
            verify(taskService, times(1)).findByIdWithPermission(taskId, userId);
        }
    }

    @Test
    void testGetTasksByProject_Success() {
        TaskDTO taskDTO = TaskDTO.fromTask(testTask);
        List<TaskDTO> taskDTOs = Arrays.asList(taskDTO);
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(taskService.findTaskDTOsByProjectId(projectId, userId)).thenReturn(taskDTOs);

            ResponseEntity<?> response = taskController.getTasksByProject(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, ((List<?>) response.getBody()).size());
            verify(taskService, times(1)).findTaskDTOsByProjectId(projectId, userId);
        }
    }

    @Test
    void testGetTasksByProject_Unauthorized() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            ResponseEntity<?> response = taskController.getTasksByProject(projectId);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).findTaskDTOsByProjectId(any(), any());
        }
    }

    @Test
    void testGetTasksByProject_Forbidden() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(taskService.findTaskDTOsByProjectId(projectId, userId))
                    .thenThrow(new RuntimeException("You must be a member of the project to view tasks."));

            ResponseEntity<?> response = taskController.getTasksByProject(projectId);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("You must be a member of the project to view tasks.", response.getBody());
            verify(taskService, times(1)).findTaskDTOsByProjectId(projectId, userId);
        }
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
    void testGetTaskHistory_Success() {
        // Given
        UUID viewerId = UUID.randomUUID();
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        List<TaskHistory> histories = Arrays.asList(history);
        when(taskService.getTaskHistory(taskId, viewerId)).thenReturn(histories);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(viewerId);

            // When
            ResponseEntity<?> response = taskController.getTaskHistory(taskId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            @SuppressWarnings("unchecked")
            List<TaskHistory> body = (List<TaskHistory>) response.getBody();
            assertEquals(1, body.size());
            verify(taskService, times(1)).getTaskHistory(taskId, viewerId);
        }
    }

    @Test
    void testGetTaskHistory_Unauthorized() {
        // Given
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            // When
            ResponseEntity<?> response = taskController.getTaskHistory(taskId);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).getTaskHistory(any(), any());
        }
    }

    @Test
    void testCreateTask_Success() {
        // Given
        UUID creatorId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setName("New Task");
        request.setDescription("Task Description");
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setPriority(Task.TaskPriority.HIGH);

        when(taskService.createTask(any(CreateTaskRequest.class), any(UUID.class))).thenReturn(testTask);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(creatorId);

            // When
            ResponseEntity<?> response = taskController.createTask(request);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(taskService, times(1)).createTask(any(CreateTaskRequest.class), eq(creatorId));
        }
    }

    @Test
    void testCreateTask_WhenUserNotAuthenticated() {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setName("New Task");

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            // When
            ResponseEntity<?> response = taskController.createTask(request);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).createTask(any(CreateTaskRequest.class), any(UUID.class));
        }
    }

    @Test
    void testCreateTask_WhenServiceThrowsException() {
        // Given
        UUID creatorId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setName("New Task");

        when(taskService.createTask(any(CreateTaskRequest.class), any(UUID.class)))
                .thenThrow(new RuntimeException("You must be a member or administrator of the project to create tasks."));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(creatorId);

            // When
            ResponseEntity<?> response = taskController.createTask(request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("You must be a member or administrator of the project to create tasks.", response.getBody());
            verify(taskService, times(1)).createTask(any(CreateTaskRequest.class), eq(creatorId));
        }
    }

    @Test
    void testUpdateTask_WhenTaskExists() {
        // Given
        UUID updaterId = UUID.randomUUID();
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Updated Task");
        when(taskService.updateTask(any(UUID.class), any(Task.class), any(UUID.class)))
                .thenReturn(updatedTask);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(updaterId);

            // When
            ResponseEntity<?> response = taskController.updateTask(taskId, updatedTask);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Updated Task", ((Task) response.getBody()).getName());
            verify(taskService, times(1)).updateTask(eq(taskId), eq(updatedTask), eq(updaterId));
        }
    }

    @Test
    void testUpdateTask_WhenTaskDoesNotExist() {
        // Given
        UUID updaterId = UUID.randomUUID();
        when(taskService.updateTask(any(UUID.class), any(Task.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Task not found"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(updaterId);

            // When
            ResponseEntity<?> response = taskController.updateTask(taskId, testTask);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Task not found", response.getBody());
            verify(taskService, times(1)).updateTask(eq(taskId), eq(testTask), eq(updaterId));
        }
    }

    @Test
    void testUpdateTask_Unauthorized() {
        // Given
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Updated Task");

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            // When
            ResponseEntity<?> response = taskController.updateTask(taskId, updatedTask);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).updateTask(any(), any(), any());
        }
    }

    @Test
    void testUpdateTask_WhenServiceThrowsException() {
        // Given
        UUID updaterId = UUID.randomUUID();
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Updated Task");

        when(taskService.updateTask(any(UUID.class), any(Task.class), any(UUID.class)))
                .thenThrow(new RuntimeException("You must be a member or administrator of the project to update tasks."));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(updaterId);

            // When
            ResponseEntity<?> response = taskController.updateTask(taskId, updatedTask);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("You must be a member or administrator of the project to update tasks.", response.getBody());
            verify(taskService, times(1)).updateTask(eq(taskId), eq(updatedTask), eq(updaterId));
        }
    }

    @Test
    void testGetTaskHistory_WhenServiceThrowsException() {
        // Given
        UUID viewerId = UUID.randomUUID();
        when(taskService.getTaskHistory(taskId, viewerId))
                .thenThrow(new RuntimeException("You must be a member of the project to view task history."));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(viewerId);

            // When
            ResponseEntity<?> response = taskController.getTaskHistory(taskId);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("You must be a member of the project to view task history.", response.getBody());
            verify(taskService, times(1)).getTaskHistory(taskId, viewerId);
        }
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
        UUID assignedById = UUID.randomUUID();
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);
        
        when(taskService.assignTask(any(UUID.class), any(AssignTaskRequest.class), any(UUID.class)))
                .thenReturn(testTask);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(assignedById);

            // When
            ResponseEntity<?> response = taskController.assignTask(taskId, request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(taskService, times(1)).assignTask(eq(taskId), eq(request), eq(assignedById));
        }
    }

    @Test
    void testAssignTask_WhenServiceThrowsException() {
        // Given
        UUID assignedById = UUID.randomUUID();
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);
        
        when(taskService.assignTask(any(UUID.class), any(AssignTaskRequest.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Task not found"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(assignedById);

            // When
            ResponseEntity<?> response = taskController.assignTask(taskId, request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(taskService, times(1)).assignTask(eq(taskId), eq(request), eq(assignedById));
        }
    }

    @Test
    void testAssignTask_Unauthorized() {
        // Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            // When
            ResponseEntity<?> response = taskController.assignTask(taskId, request);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(taskService, never()).assignTask(any(), any(), any());
        }
    }
}
