package com.codeSolution.PMT.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private EmailService emailService;

    private User testUser1;
    private User testUser2;
    private Project testProject;
    private Role memberRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        projectMemberRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Créer des rôles
        memberRole = new Role();
        memberRole.setName(Role.RoleName.MEMBER);
        memberRole = roleRepository.save(memberRole);

        adminRole = new Role();
        adminRole.setName(Role.RoleName.ADMIN);
        adminRole = roleRepository.save(adminRole);

        // Créer des utilisateurs
        testUser1 = new User();
        testUser1.setUserName("user1");
        testUser1.setEmail("user1@example.com");
        testUser1.setPassword("password123");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setUserName("user2");
        testUser2.setEmail("user2@example.com");
        testUser2.setPassword("password123");
        testUser2 = userRepository.save(testUser2);

        // Créer un projet
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject = projectRepository.save(testProject);
    }

    @Test
    void testFindAll() {
        // When
        List<Project> result = projectService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }

    @Test
    void testFindById_WhenProjectExists() {
        // When
        Optional<Project> result = projectService.findById(testProject.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProject.getId(), result.get().getId());
        assertEquals("Test Project", result.get().getName());
    }

    @Test
    void testFindById_WhenProjectDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Project> result = projectService.findById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testSave() {
        // Given
        Project newProject = new Project();
        newProject.setName("New Project");
        newProject.setDescription("New Description");

        // When
        Project result = projectService.save(newProject);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New Project", result.getName());
        assertTrue(projectRepository.findById(result.getId()).isPresent());
    }

    @Test
    void testDeleteById() {
        // Given
        UUID projectId = testProject.getId();

        // When
        projectService.deleteById(projectId);

        // Then
        assertFalse(projectRepository.findById(projectId).isPresent());
    }

    @Test
    void testInviteMemberByEmail_Success() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("user2@example.com");
        request.setRoleId(memberRole.getId());

        // When
        Project result = projectService.inviteMemberByEmail(testProject.getId(), request, testUser1.getId());

        // Then
        assertNotNull(result);
        assertTrue(projectMemberRepository.existsByProjectIdAndUserId(testProject.getId(), testUser2.getId()));
        verify(emailService).sendProjectInvitation("user2@example.com", "Test Project", "user1");
    }

    @Test
    void testInviteMemberByEmail_WhenProjectNotFound() {
        // Given
        UUID nonExistentProjectId = UUID.randomUUID();
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("user2@example.com");
        request.setRoleId(memberRole.getId());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.inviteMemberByEmail(nonExistentProjectId, request, testUser1.getId());
        });

        assertEquals("Project not found", exception.getMessage());
        verify(emailService, never()).sendProjectInvitation(anyString(), anyString(), anyString());
    }

    @Test
    void testInviteMemberByEmail_WhenUserNotFound() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("nonexistent@example.com");
        request.setRoleId(memberRole.getId());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.inviteMemberByEmail(testProject.getId(), request, testUser1.getId());
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(emailService, never()).sendProjectInvitation(anyString(), anyString(), anyString());
    }

    @Test
    void testInviteMemberByEmail_WhenUserAlreadyMember() {
        // Given
        ProjectMember existingMember = new ProjectMember();
        existingMember.setProjectId(testProject.getId());
        existingMember.setUserId(testUser2.getId());
        existingMember.setRole(memberRole);
        projectMemberRepository.save(existingMember);

        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("user2@example.com");
        request.setRoleId(memberRole.getId());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.inviteMemberByEmail(testProject.getId(), request, testUser1.getId());
        });

        assertEquals("User is already a member of this project", exception.getMessage());
    }

    @Test
    void testUpdateMemberRole_Success() {
        // Given
        ProjectMember member = new ProjectMember();
        member.setProjectId(testProject.getId());
        member.setUserId(testUser2.getId());
        member.setRole(memberRole);
        projectMemberRepository.save(member);

        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest();
        request.setRoleId(adminRole.getId());

        // When
        Project result = projectService.updateMemberRole(testProject.getId(), testUser2.getId(), request);

        // Then
        assertNotNull(result);
        Optional<ProjectMember> updatedMember = projectMemberRepository
                .findByProjectIdAndUserId(testProject.getId(), testUser2.getId());
        assertTrue(updatedMember.isPresent());
        assertEquals(adminRole.getId(), updatedMember.get().getRole().getId());
    }

    @Test
    void testUpdateMemberRole_WhenMemberNotFound() {
        // Given
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest();
        request.setRoleId(adminRole.getId());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateMemberRole(testProject.getId(), testUser2.getId(), request);
        });

        assertEquals("Member not found in project", exception.getMessage());
    }

    @Test
    void testRemoveMember_Success() {
        // Given
        ProjectMember member = new ProjectMember();
        member.setProjectId(testProject.getId());
        member.setUserId(testUser2.getId());
        member.setRole(memberRole);
        projectMemberRepository.save(member);

        // When
        Project result = projectService.removeMember(testProject.getId(), testUser2.getId());

        // Then
        assertNotNull(result);
        assertFalse(projectMemberRepository.existsByProjectIdAndUserId(testProject.getId(), testUser2.getId()));
    }

    @Test
    void testGetMemberRole_WhenMemberExists() {
        // Given
        ProjectMember member = new ProjectMember();
        member.setProjectId(testProject.getId());
        member.setUserId(testUser2.getId());
        member.setRole(memberRole);
        projectMemberRepository.save(member);

        // When
        Optional<Role> result = projectService.getMemberRole(testProject.getId(), testUser2.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(memberRole.getId(), result.get().getId());
    }

    @Test
    void testGetMemberRole_WhenMemberDoesNotExist() {
        // When
        Optional<Role> result = projectService.getMemberRole(testProject.getId(), testUser2.getId());

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetProjectMembers() {
        // Given
        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(testProject.getId());
        member1.setUserId(testUser1.getId());
        member1.setRole(adminRole);
        projectMemberRepository.save(member1);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(testProject.getId());
        member2.setUserId(testUser2.getId());
        member2.setRole(memberRole);
        projectMemberRepository.save(member2);

        // When
        List<ProjectMember> result = projectService.getProjectMembers(testProject.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(m -> m.getUserId().equals(testUser1.getId())));
        assertTrue(result.stream().anyMatch(m -> m.getUserId().equals(testUser2.getId())));
    }
}
