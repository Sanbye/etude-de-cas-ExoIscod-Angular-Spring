package com.codeSolution.PMT.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ProjectMemberId implements Serializable {
    private UUID projectId;
    private UUID userId;
}

