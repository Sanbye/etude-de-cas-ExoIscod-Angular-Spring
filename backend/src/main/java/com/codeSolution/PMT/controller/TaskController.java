package com.codeSolution.PMT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codeSolution.PMT.dto.AssignTaskRequest;
import com.codeSolution.PMT.dto.CreateTaskRequest;
import com.codeSolution.PMT.dto.TaskDTO;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.model.TaskHistory;
import com.codeSolution.PMT.service.TaskService;
import com.codeSolution.PMT.util.SecurityUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable UUID projectId) {
        List<TaskDTO> tasks = taskService.findTaskDTOsByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByProjectAndStatus(@PathVariable UUID projectId, 
                                                                 @PathVariable String status) {
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            List<Task> tasks = taskService.findByProjectIdAndStatus(projectId, taskStatus);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignedUser(@PathVariable UUID userId) {
        List<Task> tasks = taskService.findByAssignedUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable UUID id) {
        List<TaskHistory> history = taskService.getTaskHistory(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        try {
            UUID creatorId = SecurityUtil.getCurrentUserId();
            if (creatorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Task savedTask = taskService.createTask(request, creatorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody Task task) {
        try {
            // Pour l'instant, on utilise des UUID par défaut. Dans un vrai projet, on récupérerait depuis le token JWT
            UUID projectMemberProjectId = UUID.fromString("20000000-0000-0000-0000-000000000001"); // TODO: Récupérer depuis le token JWT
            UUID projectMemberUserId = UUID.fromString("10000000-0000-0000-0000-000000000001"); // TODO: Récupérer depuis le token JWT
            Task updatedTask = taskService.updateTask(id, task, projectMemberProjectId, projectMemberUserId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        if (taskService.findById(id).isPresent()) {
            taskService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTask(@PathVariable UUID taskId, 
                                        @RequestBody AssignTaskRequest request) {
        try {
            UUID assignedById = SecurityUtil.getCurrentUserId();
            if (assignedById == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Task task = taskService.assignTask(taskId, request, assignedById);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

