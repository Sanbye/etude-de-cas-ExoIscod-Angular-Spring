package com.codeSolution.PNT.dto;

import com.codeSolution.PNT.model.ProjectMember;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    private ProjectMember.ProjectRole role;
}

