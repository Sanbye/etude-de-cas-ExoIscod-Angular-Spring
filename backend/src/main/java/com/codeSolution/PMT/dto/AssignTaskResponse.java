package com.codeSolution.PMT.dto;

import com.codeSolution.PMT.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTaskResponse {
    private Task task;
    private String userEmail;
    private String taskTitle;
    private String projectName;
    private boolean emailSent;
}
