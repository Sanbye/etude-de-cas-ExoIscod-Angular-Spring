package com.codeSolution.PMT.dto;

import com.codeSolution.PMT.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {
    private UUID projectId;
    private UUID userId;
    private String userEmail;
    private String userName;
    private Role role;
}
