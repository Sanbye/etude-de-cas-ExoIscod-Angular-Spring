package com.codeSolution.PMT.dto;

import com.codeSolution.PMT.model.Task;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskDTO {
    private UUID id;
    private String name;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private LocalDate dueDate;
    private LocalDate endDate;
    private UUID projectId;
    private UUID assignedUserId;

    public static TaskDTO fromTask(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setEndDate(task.getEndDate());
        
        if (task.getProjectMember() != null) {
            dto.setProjectId(task.getProjectMember().getProjectId());
            dto.setAssignedUserId(task.getProjectMember().getUserId());
        }
        
        return dto;
    }
}
