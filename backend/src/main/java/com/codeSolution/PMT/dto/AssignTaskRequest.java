package com.codeSolution.PMT.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignTaskRequest {
    private UUID projectId;
    private UUID userId;
}
