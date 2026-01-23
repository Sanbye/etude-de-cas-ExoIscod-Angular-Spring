package com.codeSolution.PMT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeSolution.PMT.dto.AssignTaskRequest;
import com.codeSolution.PMT.dto.CreateTaskRequest;
import com.codeSolution.PMT.dto.TaskDTO;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
import com.codeSolution.PMT.repository.ProjectMemberRepository;
import com.codeSolution.PMT.repository.TaskHistoryRepository;
import com.codeSolution.PMT.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final EmailService emailService;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(UUID id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<TaskDTO> findTaskDTOsByProjectId(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(TaskDTO::fromTask)
                .toList();
    }

    public List<Task> findByAssignedUserId(UUID userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> findByProjectIdAndStatus(UUID projectId, Task.TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status);
    }

    public Task save(Task task, UUID projectMemberProjectId, UUID projectMemberUserId) {
        boolean isNew = task.getId() == null;
        
        if (isNew && task.getProjectMember() == null) {
            ProjectMember projectMember = projectMemberRepository
                    .findByProjectIdAndUserId(projectMemberProjectId, projectMemberUserId)
                    .orElseThrow(() -> new RuntimeException("ProjectMember not found"));
            task.setProjectMember(projectMember);
        }
        
        Task savedTask = taskRepository.save(task);
        
        if (isNew) {
            ProjectMember projectMember = savedTask.getProjectMember();
            createHistoryEntry(savedTask, projectMember, TaskHistory.FieldName.name, null, savedTask.getName());
        }
        
        return savedTask;
    }

    public Task createTask(CreateTaskRequest request, UUID creatorId) {
        ProjectMember projectMember = projectMemberRepository
                .findByProjectIdAndUserId(request.getProjectId(), creatorId)
                .orElseThrow(() -> new RuntimeException("You must be a member or administrator of the project to create tasks."));
        
        if (projectMember.getRole() != Role.ADMIN && 
            projectMember.getRole() != Role.MEMBER) {
            throw new RuntimeException("You must be a member or administrator of the project to create tasks.");
        }

        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM);
        task.setStatus(Task.TaskStatus.TODO);
        task.setProjectMember(projectMember);

        Task savedTask = taskRepository.save(task);

        // Créer une entrée d'historique
        createHistoryEntry(savedTask, projectMember, TaskHistory.FieldName.name, null, savedTask.getName());

        return savedTask;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(UUID id) {
        taskRepository.deleteById(id);
    }

    public Task assignToProjectMember(UUID taskId, UUID projectMemberProjectId, UUID projectMemberUserId, UUID assignedByProjectMemberProjectId, UUID assignedByProjectMemberUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        ProjectMember projectMember = projectMemberRepository
                .findByProjectIdAndUserId(projectMemberProjectId, projectMemberUserId)
                .orElseThrow(() -> new RuntimeException("ProjectMember not found"));
        
        ProjectMember previousProjectMember = task.getProjectMember();
        task.setProjectMember(projectMember);
        Task savedTask = taskRepository.save(task);
        
        // Créer une entrée d'historique
        ProjectMember assignedByProjectMember = projectMemberRepository
                .findByProjectIdAndUserId(assignedByProjectMemberProjectId, assignedByProjectMemberUserId)
                .orElseThrow(() -> new RuntimeException("Assigned by ProjectMember not found"));
        
        createHistoryEntry(savedTask, assignedByProjectMember, TaskHistory.FieldName.projectMembers, 
                previousProjectMember != null ? previousProjectMember.getUser().getEmail() : null, 
                projectMember.getUser().getEmail());
        
        // Envoyer une notification email
        emailService.sendTaskAssignmentNotification(
                projectMember.getUser().getEmail(), 
                task.getName(), 
                projectMember.getProject().getName()
        );
        
        return savedTask;
    }

    public Task assignTask(UUID taskId, AssignTaskRequest request, UUID assignedById) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        UUID taskProjectId = task.getProjectMember().getProjectId();
        
        ProjectMember assignedByMember = projectMemberRepository
                .findByProjectIdAndUserId(taskProjectId, assignedById)
                .orElseThrow(() -> new RuntimeException("You must be a member or administrator of the project to assign tasks."));
        
        if (assignedByMember.getRole() != Role.ADMIN && assignedByMember.getRole() != Role.MEMBER) {
            throw new RuntimeException("You must be a member or administrator of the project to assign tasks.");
        }
        
        // Vérifier que le membre à assigner appartient au même projet
        ProjectMember assigneeMember = projectMemberRepository
                .findByProjectIdAndUserId(request.getProjectId(), request.getUserId())
                .orElseThrow(() -> new RuntimeException("The user is not a member of this project."));
        
        if (!assigneeMember.getProjectId().equals(taskProjectId)) {
            throw new RuntimeException("The user must be a member of the same project as the task.");
        }
        
        ProjectMember previousProjectMember = task.getProjectMember();
        task.setProjectMember(assigneeMember);
        Task savedTask = taskRepository.save(task);
        
        createHistoryEntry(savedTask, assignedByMember, TaskHistory.FieldName.projectMembers, 
                previousProjectMember != null ? previousProjectMember.getUser().getEmail() : null, 
                assigneeMember.getUser().getEmail());
        
        emailService.sendTaskAssignmentNotification(
                assigneeMember.getUser().getEmail(), 
                task.getName(), 
                assigneeMember.getProject().getName()
        );
        
        return savedTask;
    }

    public Task updateTask(UUID taskId, Task updatedTask, UUID updaterId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        ProjectMember taskProjectMember = existingTask.getProjectMember();
        if (taskProjectMember == null) {
            throw new RuntimeException("Task is not assigned to a project member");
        }
        
        UUID projectId = taskProjectMember.getProjectId();
        

        ProjectMember projectMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, updaterId)
                .orElseThrow(() -> new RuntimeException("You must be a member or administrator of the project to update tasks."));
        
        if (projectMember.getRole() != Role.ADMIN && projectMember.getRole() != Role.MEMBER) {
            throw new RuntimeException("You must be a member or administrator of the project to update tasks.");
        }
        
        // Tracker les changements
        if (!existingTask.getName().equals(updatedTask.getName())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.name, 
                    existingTask.getName(), updatedTask.getName());
        }
        if (!existingTask.getDescription().equals(updatedTask.getDescription())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.description, 
                    existingTask.getDescription(), updatedTask.getDescription());
        }
        if (!existingTask.getStatus().equals(updatedTask.getStatus())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.status, 
                    existingTask.getStatus().toString(), updatedTask.getStatus().toString());
        }
        if (!existingTask.getPriority().equals(updatedTask.getPriority())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.priority, 
                    existingTask.getPriority().toString(), updatedTask.getPriority().toString());
        }
        if (existingTask.getDueDate() != null && !existingTask.getDueDate().equals(updatedTask.getDueDate()) ||
            updatedTask.getDueDate() != null && !updatedTask.getDueDate().equals(existingTask.getDueDate())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.dueDate, 
                    existingTask.getDueDate() != null ? existingTask.getDueDate().toString() : null, 
                    updatedTask.getDueDate() != null ? updatedTask.getDueDate().toString() : null);
        }
        if (existingTask.getEndDate() != null && !existingTask.getEndDate().equals(updatedTask.getEndDate()) ||
            updatedTask.getEndDate() != null && !updatedTask.getEndDate().equals(existingTask.getEndDate())) {
            createHistoryEntry(existingTask, projectMember, TaskHistory.FieldName.endDate, 
                    existingTask.getEndDate() != null ? existingTask.getEndDate().toString() : null, 
                    updatedTask.getEndDate() != null ? updatedTask.getEndDate().toString() : null);
        }
        
        // Mettre à jour les champs
        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setEndDate(updatedTask.getEndDate());
        
        return taskRepository.save(existingTask);
    }

    public List<TaskHistory> getTaskHistory(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        ProjectMember taskProjectMember = task.getProjectMember();
        if (taskProjectMember == null) {
            throw new RuntimeException("Task is not assigned to a project member");
        }
        
        UUID projectId = taskProjectMember.getProjectId();
        
        projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("You must be a member of the project to view task history."));
        
        return taskHistoryRepository.findByTaskIdOrderByModifiedAtDesc(taskId);
    }

    private void createHistoryEntry(Task task, ProjectMember projectMember, TaskHistory.FieldName fieldName, 
                                    String oldValue, String newValue) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setProjectMember(projectMember);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        
        taskHistoryRepository.save(history);
    }
}

