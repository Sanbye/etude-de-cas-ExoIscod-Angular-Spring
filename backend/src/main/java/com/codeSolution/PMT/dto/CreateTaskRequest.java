package com.codeSolution.PMT.dto;

import com.codeSolution.PMT.model.Task;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    private UUID projectId;
    private String name;
    private String description;
    private LocalDate dueDate;
    private Task.TaskPriority priority;
}
