package com.codeSolution.PNT.controller;

import com.codeSolution.PNT.dto.InviteMemberRequest;
import com.codeSolution.PNT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PNT.model.Project;
import com.codeSolution.PNT.model.ProjectMember;
import com.codeSolution.PNT.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Project>> getProjectsByOwner(@PathVariable Long ownerId) {
        List<Project> projects = projectService.findByOwnerId(ownerId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Project>> getProjectsByMember(@PathVariable Long memberId) {
        List<Project> projects = projectService.findByMemberId(memberId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMember> members = projectService.getProjectMembers(id);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return projectService.findById(id)
                .map(existingProject -> {
                    project.setId(id);
                    Project updatedProject = projectService.save(project);
                    return ResponseEntity.ok(updatedProject);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectService.findById(id).isPresent()) {
            projectService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{projectId}/invite")
    public ResponseEntity<?> inviteMember(@PathVariable Long projectId, 
                                         @RequestBody InviteMemberRequest request) {
        try {
            // Pour l'instant, on utilise un userId par défaut. Dans un vrai projet, on récupérerait l'ID depuis le token JWT
            Long inviterId = 1L; // TODO: Récupérer depuis le token JWT
            Project project = projectService.inviteMemberByEmail(projectId, request, inviterId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable Long projectId, 
                                              @PathVariable Long userId,
                                              @RequestBody UpdateMemberRoleRequest request) {
        try {
            Project project = projectService.updateMemberRole(projectId, userId, request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Project> removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        try {
            Project project = projectService.removeMember(projectId, userId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

