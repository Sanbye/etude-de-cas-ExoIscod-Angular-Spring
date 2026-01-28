package com.codeSolution.PMT.service;

import com.codeSolution.PMT.dto.AssignTaskRequest;
import com.codeSolution.PMT.dto.CreateTaskRequest;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.repository.ProjectMemberRepository;
import com.codeSolution.PMT.repository.TaskHistoryRepository;
import com.codeSolution.PMT.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskHistoryRepository taskHistoryRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

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

        User testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");

        Project testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");

        testProjectMember = new ProjectMember();
        testProjectMember.setProjectId(projectId);
        testProjectMember.setUserId(userId);
        testProjectMember.setUser(testUser);
        testProjectMember.setProject(testProject);

        testTask = new Task();
        testTask.setId(taskId);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setPriority(Task.TaskPriority.MEDIUM);
        testTask.setProjectMember(testProjectMember);
    }

    @Test
    void testFindAll() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        // When
        List<Task> result = taskService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask, result.get(0));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenTaskExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        Optional<Task> result = taskService.findById(taskId);

        assertTrue(result.isPresent());
        assertEquals(testTask, result.get());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testFindById_WhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.findById(taskId);

        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testFindByIdWithPermission_Success() {
        UUID viewerId = userId;
        testProjectMember.setRole(Role.MEMBER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, viewerId))
                .thenReturn(Optional.of(testProjectMember));

        Task result = taskService.findByIdWithPermission(taskId, viewerId);

        assertNotNull(result);
        assertEquals(testTask, result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, viewerId);
    }

    @Test
    void testFindByIdWithPermission_WhenTaskDoesNotExist() {
        UUID viewerId = userId;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findByIdWithPermission(taskId, viewerId);
        });

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, never()).findByProjectIdAndUserId(any(), any());
    }

    @Test
    void testFindByIdWithPermission_WhenTaskNotAssigned() {
        UUID viewerId = userId;
        Task taskWithoutMember = new Task();
        taskWithoutMember.setId(taskId);
        taskWithoutMember.setProjectMember(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskWithoutMember));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findByIdWithPermission(taskId, viewerId);
        });

        assertEquals("Task is not assigned to a project member", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, never()).findByProjectIdAndUserId(any(), any());
    }

    @Test
    void testFindByIdWithPermission_WhenUserNotMember() {
        UUID viewerId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, viewerId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findByIdWithPermission(taskId, viewerId);
        });

        assertEquals("You must be a member of the project to view task details.", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, viewerId);
    }

    @Test
    void testFindByIdWithPermission_WithObserverRole() {
        UUID viewerId = userId;
        testProjectMember.setRole(Role.OBSERVER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, viewerId))
                .thenReturn(Optional.of(testProjectMember));

        Task result = taskService.findByIdWithPermission(taskId, viewerId);

        assertNotNull(result);
        assertEquals(testTask, result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, viewerId);
    }

    @Test
    void testSave_WithProjectMember() {
        // Given
        Task newTask = new Task();
        newTask.setName("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(Task.TaskStatus.TODO);
        newTask.setPriority(Task.TaskPriority.LOW);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(taskId);
            return task;
        });
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // When
        Task result = taskService.save(newTask, projectId, userId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, userId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskHistoryRepository, times(1)).save(any(TaskHistory.class));
    }

    @Test
    void testSave_Simple() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.save(testTask);

        // Then
        assertNotNull(result);
        assertEquals(testTask, result);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void testDeleteById() {
        // When
        taskService.deleteById(taskId);

        // Then
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testUpdateTask_Success() {
        // Given
        UUID updaterId = userId;
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(Task.TaskPriority.HIGH);
        updatedTask.setDueDate(LocalDate.now().plusDays(7));

        testProjectMember.setRole(Role.MEMBER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, updaterId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // When
        Task result = taskService.updateTask(taskId, updatedTask, updaterId);

        // Then
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, updaterId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskHistoryRepository, atLeastOnce()).save(any(TaskHistory.class));
    }

    @Test
    void testGetTaskHistory() {
        // Given
        UUID viewerId = userId;
        TaskHistory history = new TaskHistory();
        List<TaskHistory> histories = Arrays.asList(history);
        
        testProjectMember.setRole(Role.MEMBER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, viewerId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskHistoryRepository.findByTaskIdOrderByModifiedAtDesc(taskId)).thenReturn(histories);

        // When
        List<TaskHistory> result = taskService.getTaskHistory(taskId, viewerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(history, result.get(0));
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, viewerId);
        verify(taskHistoryRepository, times(1)).findByTaskIdOrderByModifiedAtDesc(taskId);
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

        testProjectMember.setRole(Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, creatorId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(taskId);
            return task;
        });
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // When
        Task result = taskService.createTask(request, creatorId);

        // Then
        assertNotNull(result);
        assertEquals("New Task", result.getName());
        assertEquals("Task Description", result.getDescription());
        assertEquals(Task.TaskPriority.HIGH, result.getPriority());
        assertEquals(Task.TaskStatus.TODO, result.getStatus());
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, creatorId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskHistoryRepository, times(1)).save(any(TaskHistory.class));
    }

    @Test
    void testCreateTask_WhenUserNotMember() {
        // Given
        UUID creatorId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setName("New Task");

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, creatorId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.createTask(request, creatorId);
        });

        assertEquals("You must be a member or administrator of the project to create tasks.", exception.getMessage());
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, creatorId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_WhenProjectMemberIsObserver() {
        // Given
        UUID creatorId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setName("New Task");

        testProjectMember.setRole(Role.OBSERVER);
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, creatorId))
                .thenReturn(Optional.of(testProjectMember));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.createTask(request, creatorId);
        });

        assertEquals("You must be a member or administrator of the project to create tasks.", exception.getMessage());
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, creatorId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_WhenUserNotMember() {
        // Given
        UUID updaterId = UUID.randomUUID();
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, updaterId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(taskId, updatedTask, updaterId);
        });

        assertEquals("You must be a member or administrator of the project to update tasks.", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, updaterId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_WhenProjectMemberIsObserver() {
        // Given
        UUID updaterId = userId;
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");

        testProjectMember.setRole(Role.OBSERVER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, updaterId))
                .thenReturn(Optional.of(testProjectMember));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(taskId, updatedTask, updaterId);
        });

        assertEquals("You must be a member or administrator of the project to update tasks.", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, updaterId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_WithEndDate() {
        // Given
        UUID updaterId = userId;
        Task updatedTask = new Task();
        updatedTask.setName(testTask.getName());
        updatedTask.setDescription(testTask.getDescription());
        updatedTask.setStatus(testTask.getStatus());
        updatedTask.setPriority(testTask.getPriority());
        updatedTask.setDueDate(testTask.getDueDate());
        updatedTask.setEndDate(LocalDate.now());

        testProjectMember.setRole(Role.MEMBER);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, updaterId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // When
        Task result = taskService.updateTask(taskId, updatedTask, updaterId);

        // Then
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, updaterId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskHistoryRepository, atLeastOnce()).save(any(TaskHistory.class));
    }

    @Test
    void testUpdateTask_WhenTaskNotAssigned() {
        // Given
        UUID updaterId = userId;
        Task taskWithoutMember = new Task();
        taskWithoutMember.setId(taskId);
        taskWithoutMember.setName("Task without member");
        taskWithoutMember.setProjectMember(null);

        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskWithoutMember));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(taskId, updatedTask, updaterId);
        });

        assertEquals("Task is not assigned to a project member", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, never()).findByProjectIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetTaskHistory_WhenUserNotMember() {
        // Given
        UUID viewerId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, viewerId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskHistory(taskId, viewerId);
        });

        assertEquals("You must be a member of the project to view task history.", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, viewerId);
        verify(taskHistoryRepository, never()).findByTaskIdOrderByModifiedAtDesc(any());
    }

    @Test
    void testGetTaskHistory_WhenTaskNotAssigned() {
        // Given
        UUID viewerId = userId;
        Task taskWithoutMember = new Task();
        taskWithoutMember.setId(taskId);
        taskWithoutMember.setProjectMember(null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskWithoutMember));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskHistory(taskId, viewerId);
        });

        assertEquals("Task is not assigned to a project member", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, never()).findByProjectIdAndUserId(any(), any());
        verify(taskHistoryRepository, never()).findByTaskIdOrderByModifiedAtDesc(any());
    }

    @Test
    void testAssignTask_Success() {
        UUID assignedById = userId;
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);

        User assigneeUser = new User();
        assigneeUser.setId(userId);
        assigneeUser.setEmail("assignee@example.com");

        Project project = new Project();
        project.setId(projectId);
        project.setName("Project Name");

        ProjectMember assignedByMember = new ProjectMember();
        assignedByMember.setProjectId(projectId);
        assignedByMember.setUserId(assignedById);
        assignedByMember.setRole(Role.ADMIN);

        ProjectMember assigneeMember = new ProjectMember();
        assigneeMember.setProjectId(projectId);
        assigneeMember.setUserId(userId);
        assigneeMember.setRole(Role.MEMBER);
        assigneeMember.setUser(assigneeUser);
        assigneeMember.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, assignedById))
                .thenReturn(Optional.of(assignedByMember));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(assigneeMember));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        Task result = taskService.assignTask(taskId, request, assignedById);

        assertNotNull(result);
        verify(notificationService, times(1)).createTaskAssignmentNotification(any(ProjectMember.class), any(Task.class));
        verify(emailService, times(1)).sendTaskAssignmentNotification(anyString(), anyString(), anyString());
    }

    @Test
    void testAssignTask_WhenAssignedByNotMember() {
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.assignTask(taskId, request, userId));

        assertEquals("You must be a member or administrator of the project to assign tasks.", exception.getMessage());
    }

    @Test
    void testAssignTask_WhenAssignedByObserver() {
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(userId);

        ProjectMember assignedByMember = new ProjectMember();
        assignedByMember.setProjectId(projectId);
        assignedByMember.setUserId(userId);
        assignedByMember.setRole(Role.OBSERVER);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(assignedByMember));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.assignTask(taskId, request, userId));

        assertEquals("You must be a member or administrator of the project to assign tasks.", exception.getMessage());
    }

    @Test
    void testAssignTask_WhenAssigneeNotMember() {
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(UUID.randomUUID());

        ProjectMember assignedByMember = new ProjectMember();
        assignedByMember.setProjectId(projectId);
        assignedByMember.setUserId(userId);
        assignedByMember.setRole(Role.ADMIN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(assignedByMember));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, request.getUserId()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.assignTask(taskId, request, userId));

        assertEquals("The user is not a member of this project.", exception.getMessage());
    }

    @Test
    void testAssignTask_WhenAssigneeDifferentProject() {
        AssignTaskRequest request = new AssignTaskRequest();
        request.setProjectId(projectId);
        request.setUserId(UUID.randomUUID());

        ProjectMember assignedByMember = new ProjectMember();
        assignedByMember.setProjectId(projectId);
        assignedByMember.setUserId(userId);
        assignedByMember.setRole(Role.ADMIN);

        ProjectMember assigneeMember = new ProjectMember();
        assigneeMember.setProjectId(UUID.randomUUID());
        assigneeMember.setUserId(request.getUserId());
        assigneeMember.setRole(Role.MEMBER);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(assignedByMember));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, request.getUserId()))
                .thenReturn(Optional.of(assigneeMember));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.assignTask(taskId, request, userId));

        assertEquals("The user must be a member of the same project as the task.", exception.getMessage());
    }

    @Test
    void testGetTaskHistory_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.getTaskHistory(taskId, userId));

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void testFindTaskDTOsByProjectId_WhenNotMember() {
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.findTaskDTOsByProjectId(projectId, userId));

        assertEquals("You must be a member of the project to view tasks.", exception.getMessage());
    }
}

