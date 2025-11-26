package com.codeSolution.PMT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.ProjectMemberRepository;
import com.codeSolution.PMT.repository.ProjectRepository;
import com.codeSolution.PMT.repository.RoleRepository;
import com.codeSolution.PMT.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Optional<Project> findById(UUID id) {
        return projectRepository.findById(id);
    }

    public List<Project> findByMemberId(UUID memberId) {
        return projectRepository.findByMemberId(memberId);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void deleteById(UUID id) {
        projectRepository.deleteById(id);
    }

    public Project inviteMemberByEmail(UUID projectId, InviteMemberRequest request, UUID inviterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with email " + request.getEmail() + " not found"));
        
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new RuntimeException("User is already a member of this project");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(user.getId());
        member.setRole(role);
        projectMemberRepository.save(member);

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));
        emailService.sendProjectInvitation(user.getEmail(), project.getName(), inviter.getUserName());

        return projectRepository.findById(projectId).orElse(project);
    }

    public Project updateMemberRole(UUID projectId, UUID userId, UpdateMemberRoleRequest request) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        member.setRole(role);
        projectMemberRepository.save(member);
        
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project removeMember(UUID projectId, UUID userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));
        
        projectMemberRepository.delete(member);
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Optional<Role> getMemberRole(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(ProjectMember::getRole);
    }

    public List<ProjectMember> getProjectMembers(UUID projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }
}

