package com.codeSolution.PNT.dto;

import com.codeSolution.PNT.model.ProjectMember;
import lombok.Data;

@Data
public class InviteMemberRequest {
    private String email;
    private ProjectMember.ProjectRole role = ProjectMember.ProjectRole.MEMBER;
}

