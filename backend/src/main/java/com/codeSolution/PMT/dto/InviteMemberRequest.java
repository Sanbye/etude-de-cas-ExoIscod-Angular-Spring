package com.codeSolution.PMT.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InviteMemberRequest {
    private String email;
    private UUID roleId;
}

