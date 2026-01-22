package com.codeSolution.PMT.service;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.model.User;
import com.codeSolution.PMT.repository.ProjectMemberRepository;
import com.codeSolution.PMT.repository.ProjectRepository;
import com.codeSolution.PMT.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private User testUser;
    private ProjectMember testProjectMember;
    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");

        testUser = new User();
        testUser.setId(userId);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");

        testProjectMember = new ProjectMember();
        testProjectMember.setProjectId(projectId);
        testProjectMember.setUserId(userId);
        testProjectMember.setRole(Role.MEMBER);
    }

    @Test
    void testFindAll() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(projects);

        // When
        List<Project> result = projectService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject, result.get(0));
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenProjectExists() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        Optional<Project> result = projectService.findById(projectId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProject, result.get());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testFindById_WhenProjectDoesNotExist() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When
        Optional<Project> result = projectService.findById(projectId);

        // Then
        assertFalse(result.isPresent());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testSave() {
        // Given
        UUID creatorId = UUID.randomUUID();
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(testProjectMember);

        // When
        Project result = projectService.save(testProject, creatorId);

        // Then
        assertNotNull(result);
        assertEquals(testProject, result);
        verify(projectRepository, times(1)).save(testProject);
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    @Test
    void testDeleteById() {
        // When
        projectService.deleteById(projectId);

        // Then
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void testInviteMemberByEmail_Success() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("test@example.com");
        request.setRole(Role.MEMBER);
        UUID inviterId = UUID.randomUUID();

        ProjectMember inviterMember = new ProjectMember();
        inviterMember.setProjectId(projectId);
        inviterMember.setUserId(inviterId);
        inviterMember.setRole(Role.ADMIN);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, inviterId)).thenReturn(Optional.of(inviterMember));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(testProjectMember);
        when(userRepository.findById(inviterId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        Project result = projectService.inviteMemberByEmail(projectId, request, inviterId);

        // Then
        assertNotNull(result);
        verify(projectRepository, atLeastOnce()).findById(projectId);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, inviterId);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        verify(emailService, times(1)).sendProjectInvitation(anyString(), anyString(), anyString());
    }

    @Test
    void testInviteMemberByEmail_WhenProjectNotFound() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("test@example.com");
        request.setRole(Role.MEMBER);
        UUID inviterId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.inviteMemberByEmail(projectId, request, inviterId);
        });

        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository, times(1)).findById(projectId);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void testUpdateMemberRole_Success() {
        // Given
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest();
        request.setRole(Role.ADMIN);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(testProjectMember));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(testProjectMember);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        Project result = projectService.updateMemberRole(projectId, userId, request);

        // Then
        assertNotNull(result);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testRemoveMember_Success() {
        // Given
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(testProjectMember));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        Project result = projectService.removeMember(projectId, userId);

        // Then
        assertNotNull(result);
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository, times(1)).delete(testProjectMember);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetMemberRole_WhenMemberExists() {
        // Given
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId))
                .thenReturn(Optional.of(testProjectMember));

        // When
        Optional<Role> result = projectService.getMemberRole(projectId, userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(Role.MEMBER, result.get());
        verify(projectMemberRepository, times(1)).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testGetProjectMembers() {
        // Given
        List<ProjectMember> members = Arrays.asList(testProjectMember);
        when(projectMemberRepository.findByProjectId(projectId)).thenReturn(members);

        // When
        List<ProjectMember> result = projectService.getProjectMembers(projectId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProjectMember, result.get(0));
        verify(projectMemberRepository, times(1)).findByProjectId(projectId);
    }
}

