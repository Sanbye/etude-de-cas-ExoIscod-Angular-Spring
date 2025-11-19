package com.codeSolution.PNT.service;

import com.codeSolution.PNT.dto.InviteMemberRequest;
import com.codeSolution.PNT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PNT.model.Project;
import com.codeSolution.PNT.model.ProjectMember;
import com.codeSolution.PNT.model.User;
import com.codeSolution.PNT.repository.ProjectMemberRepository;
import com.codeSolution.PNT.repository.ProjectRepository;
import com.codeSolution.PNT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    public List<Project> findByMemberId(Long memberId) {
        return projectRepository.findByMemberId(memberId);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    public Project inviteMemberByEmail(Long projectId, InviteMemberRequest request, Long inviterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with email " + request.getEmail() + " not found"));
        
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new RuntimeException("User is already a member of this project");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(user.getId());
        member.setRole(request.getRole());
        projectMemberRepository.save(member);

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));
        emailService.sendProjectInvitation(user.getEmail(), project.getName(), inviter.getUsername());

        return projectRepository.findById(projectId).orElse(project);
    }

    public Project updateMemberRole(Long projectId, Long userId, UpdateMemberRoleRequest request) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));
        
        member.setRole(request.getRole());
        projectMemberRepository.save(member);
        
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project removeMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in project"));
        
        projectMemberRepository.delete(member);
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Optional<ProjectMember.ProjectRole> getMemberRole(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(ProjectMember::getRole);
    }

    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }
}

