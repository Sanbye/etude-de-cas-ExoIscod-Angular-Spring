package com.codeSolution.PNT.controller;

import com.codeSolution.PNT.model.Task;
import com.codeSolution.PNT.model.TaskHistory;
import com.codeSolution.PNT.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskService.findByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByProjectAndStatus(@PathVariable Long projectId, 
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
    public ResponseEntity<List<Task>> getTasksByAssignedUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.findByAssignedUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable Long id) {
        List<TaskHistory> history = taskService.getTaskHistory(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Pour l'instant, on utilise un userId par défaut. Dans un vrai projet, on récupérerait l'ID depuis le token JWT
        Long createdByUserId = 1L; // TODO: Récupérer depuis le token JWT
        Task savedTask = taskService.save(task, createdByUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            // Pour l'instant, on utilise un userId par défaut. Dans un vrai projet, on récupérerait l'ID depuis le token JWT
            Long modifiedByUserId = 1L; // TODO: Récupérer depuis le token JWT
            Task updatedTask = taskService.updateTask(id, task, modifiedByUserId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskService.findById(id).isPresent()) {
            taskService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        try {
            // Pour l'instant, on utilise un userId par défaut. Dans un vrai projet, on récupérerait l'ID depuis le token JWT
            Long assignedByUserId = 1L; // TODO: Récupérer depuis le token JWT
            Task task = taskService.assignToUser(taskId, userId, assignedByUserId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

