package com.codeSolution.PMT.service;

import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
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

        testProjectMember = new ProjectMember();
        testProjectMember.setProjectId(projectId);
        testProjectMember.setUserId(userId);

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
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // When
        Optional<Task> result = taskService.findById(taskId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTask, result.get());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testFindById_WhenTaskDoesNotExist() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.findById(taskId);

        // Then
        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(taskId);
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
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(Task.TaskPriority.HIGH);
        updatedTask.setDueDate(LocalDate.now().plusDays(7));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(testProjectMember));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        // When
        Task result = taskService.updateTask(taskId, updatedTask, projectId, userId);

        // Then
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, userId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskHistoryRepository, atLeastOnce()).save(any(TaskHistory.class));
    }

    @Test
    void testGetTaskHistory() {
        // Given
        TaskHistory history = new TaskHistory();
        List<TaskHistory> histories = Arrays.asList(history);
        when(taskHistoryRepository.findByTaskIdOrderByModifiedAtDesc(taskId)).thenReturn(histories);

        // When
        List<TaskHistory> result = taskService.getTaskHistory(taskId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(history, result.get(0));
        verify(taskHistoryRepository, times(1)).findByTaskIdOrderByModifiedAtDesc(taskId);
    }
}

