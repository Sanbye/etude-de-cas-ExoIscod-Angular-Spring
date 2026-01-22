package com.codeSolution.PMT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.ProjectMemberDTO;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.service.ProjectService;
import com.codeSolution.PMT.util.SecurityUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Project>> getProjectsByMember(@PathVariable UUID memberId) {
        List<Project> projects = projectService.findByMemberId(memberId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable UUID id) {
        List<ProjectMemberDTO> members = projectService.getProjectMembersWithUserInfo(id);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Project savedProject = projectService.save(project, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody Project project) {
        return projectService.findById(id)
                .map(existingProject -> {
                    project.setId(id);
                    Project updatedProject = projectService.save(project, SecurityUtil.getCurrentUserId());
                    return ResponseEntity.ok(updatedProject);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        if (projectService.findById(id).isPresent()) {
            projectService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{projectId}/invite")
    public ResponseEntity<?> inviteMember(@PathVariable UUID projectId, 
                                         @RequestBody InviteMemberRequest request) {
        try {
            UUID inviterId = SecurityUtil.getCurrentUserId();
            if (inviterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Project project = projectService.inviteMemberByEmail(projectId, request, inviterId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable UUID projectId, 
                                              @PathVariable UUID userId,
                                              @RequestBody UpdateMemberRoleRequest request) {
        try {
            Project project = projectService.updateMemberRole(projectId, userId, request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Project> removeMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        try {
            Project project = projectService.removeMember(projectId, userId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

