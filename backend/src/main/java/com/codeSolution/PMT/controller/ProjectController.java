package com.codeSolution.PMT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.service.ProjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable UUID id) {
        List<ProjectMember> members = projectService.getProjectMembers(id);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody Project project) {
        return projectService.findById(id)
                .map(existingProject -> {
                    project.setId(id);
                    Project updatedProject = projectService.save(project);
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
            // Pour l'instant, on utilise un userId par défaut. Dans un vrai projet, on récupérerait l'ID depuis le token JWT
            UUID inviterId = UUID.fromString("10000000-0000-0000-0000-000000000001"); // TODO: Récupérer depuis le token JWT
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

