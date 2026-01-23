package com.codeSolution.PMT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.ProjectMemberDTO;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.ProjectMemberRepository;
import com.codeSolution.PMT.repository.ProjectRepository;
import com.codeSolution.PMT.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
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

    public Project save(@NonNull Project project, @NonNull UUID creatorId) {

        Project savedProject = projectRepository.save(project);

        ProjectMember creatorMember = new ProjectMember();
        creatorMember.setProjectId(savedProject.getId());
        creatorMember.setUserId(creatorId);
        creatorMember.setRole(Role.ADMIN);
        projectMemberRepository.save(creatorMember);
        
        return savedProject;
    }

    public void deleteById(UUID id) {
        projectRepository.deleteById(id);
    }

    public Project inviteMemberByEmail(UUID projectId, InviteMemberRequest request, UUID inviterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        ProjectMember inviterMember = projectMemberRepository.findByProjectIdAndUserId(projectId, inviterId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));
        
        if (inviterMember.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only project administrators can invite members");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with email " + request.getEmail() + " not found"));
        
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new RuntimeException("User is already a member of this project");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(user.getId());
        member.setRole(request.getRole());
        projectMemberRepository.save(member);

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));
        emailService.sendProjectInvitation(user.getEmail(), project.getName(), inviter.getUserName());

        return projectRepository.findById(projectId).orElse(project);
    }

    public Project updateMemberRole(UUID projectId, UUID userId, UpdateMemberRoleRequest request, UUID modifierId) {
        if (!isProjectAdmin(projectId, modifierId)) {
            throw new RuntimeException("Only project administrators can update member roles.");
        }

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));
        
        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }
        
        member.setRole(request.getRole());
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

    public List<ProjectMemberDTO> getProjectMembersWithUserInfo(UUID projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return members.stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId())
                            .orElse(null);
                    return new ProjectMemberDTO(
                            member.getProjectId(),
                            member.getUserId(),
                            user != null ? user.getEmail() : null,
                            user != null ? user.getUserName() : null,
                            member.getRole()
                    );
                })
                .collect(Collectors.toList());
    }

    public boolean isProjectAdmin(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(member -> member.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public boolean isProjectMember(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(member -> member.getRole() == Role.ADMIN || member.getRole() == Role.MEMBER)
                .orElse(false);
    }
}

