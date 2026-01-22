package com.codeSolution.PMT.dto;

import com.codeSolution.PMT.model.Role;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    private Role role;
}

