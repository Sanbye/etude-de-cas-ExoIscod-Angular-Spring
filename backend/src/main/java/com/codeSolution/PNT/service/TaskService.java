package com.codeSolution.PNT.service;

import com.codeSolution.PNT.model.Task;
import com.codeSolution.PNT.model.TaskHistory;
import com.codeSolution.PNT.model.User;
import com.codeSolution.PNT.repository.ProjectRepository;
import com.codeSolution.PNT.repository.TaskHistoryRepository;
import com.codeSolution.PNT.repository.TaskRepository;
import com.codeSolution.PNT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final EmailService emailService;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> findByAssignedUserId(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status);
    }

    public Task save(Task task, Long modifiedByUserId) {
        boolean isNew = task.getId() == null;
        Task savedTask = taskRepository.save(task);
        
        if (isNew) {
            createHistoryEntry(savedTask, modifiedByUserId, "CREATED", null, null, null);
        }
        
        return savedTask;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public Task assignToUser(Long taskId, Long userId, Long assignedByUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User previousUser = task.getAssignedUser();
        task.setAssignedUser(user);
        Task savedTask = taskRepository.save(task);
        
        // Créer une entrée d'historique
        createHistoryEntry(savedTask, assignedByUserId, "ASSIGNED", 
                "assignedUser", 
                previousUser != null ? previousUser.getEmail() : null, 
                user.getEmail());
        
        // Envoyer une notification email
        emailService.sendTaskAssignmentNotification(
                user.getEmail(), 
                task.getTitle(), 
                task.getProject().getName()
        );
        
        return savedTask;
    }

    public Task updateTask(Long taskId, Task updatedTask, Long modifiedByUserId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Tracker les changements
        if (!existingTask.getTitle().equals(updatedTask.getTitle())) {
            createHistoryEntry(existingTask, modifiedByUserId, "UPDATED", 
                    "title", existingTask.getTitle(), updatedTask.getTitle());
        }
        if (!existingTask.getStatus().equals(updatedTask.getStatus())) {
            createHistoryEntry(existingTask, modifiedByUserId, "STATUS_CHANGED", 
                    "status", existingTask.getStatus().toString(), updatedTask.getStatus().toString());
        }
        if (!existingTask.getPriority().equals(updatedTask.getPriority())) {
            createHistoryEntry(existingTask, modifiedByUserId, "UPDATED", 
                    "priority", existingTask.getPriority().toString(), updatedTask.getPriority().toString());
        }
        
        // Mettre à jour les champs
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setEndDate(updatedTask.getEndDate());
        
        return taskRepository.save(existingTask);
    }

    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    private void createHistoryEntry(Task task, Long userId, String changeType, 
                                    String fieldName, String oldValue, String newValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setModifiedBy(user);
        history.setChangeType(changeType);
        history.setFieldName(fieldName != null ? fieldName : "");
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        
        taskHistoryRepository.save(history);
    }
}

