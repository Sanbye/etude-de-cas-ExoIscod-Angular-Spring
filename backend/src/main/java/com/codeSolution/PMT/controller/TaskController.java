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
    public ResponseEntity<?> getTaskById(@PathVariable UUID id) {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Task task = taskService.findByIdWithPermission(id, userId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getTasksByProject(@PathVariable UUID projectId) {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<TaskDTO> tasks = taskService.findTaskDTOsByProjectId(projectId, userId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
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
    public ResponseEntity<?> getTaskHistory(@PathVariable UUID id) {
        try {
            UUID userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<TaskHistory> history = taskService.getTaskHistory(id, userId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> updateTask(@PathVariable UUID id, @RequestBody Task task) {
        try {
            UUID updaterId = SecurityUtil.getCurrentUserId();
            if (updaterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Task updatedTask = taskService.updateTask(id, task, updaterId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

