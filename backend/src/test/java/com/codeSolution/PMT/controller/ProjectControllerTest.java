package com.codeSolution.PMT.controller;

import com.codeSolution.PMT.dto.InviteMemberRequest;
import com.codeSolution.PMT.dto.ProjectMemberDTO;
import com.codeSolution.PMT.dto.UpdateMemberRoleRequest;
import com.codeSolution.PMT.model.Project;
import com.codeSolution.PMT.model.Role;
import com.codeSolution.PMT.service.ProjectService;
import com.codeSolution.PMT.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private Project testProject;
    private UUID projectId;
    private UUID userId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        memberId = UUID.randomUUID();

        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
    }

    @Test
    void testGetAllProjects() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(projectService.findAll()).thenReturn(projects);

        // When
        ResponseEntity<List<Project>> response = projectController.getAllProjects();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testProject, response.getBody().get(0));
        verify(projectService, times(1)).findAll();
    }

    @Test
    void testGetProjectById_WhenProjectExists() {
        // Given
        when(projectService.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        ResponseEntity<Project> response = projectController.getProjectById(projectId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProject, response.getBody());
        verify(projectService, times(1)).findById(projectId);
    }

    @Test
    void testGetProjectById_WhenProjectDoesNotExist() {
        // Given
        when(projectService.findById(projectId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Project> response = projectController.getProjectById(projectId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).findById(projectId);
    }

    @Test
    void testGetProjectsByMember() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(projectService.findByMemberId(memberId)).thenReturn(projects);

        // When
        ResponseEntity<List<Project>> response = projectController.getProjectsByMember(memberId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(projectService, times(1)).findByMemberId(memberId);
    }

    @Test
    void testGetProjectMembers() {
        // Given
        ProjectMemberDTO memberDTO = new ProjectMemberDTO(projectId, userId, "user@example.com", "testuser", Role.MEMBER);
        List<ProjectMemberDTO> members = Arrays.asList(memberDTO);
        when(projectService.getProjectMembersWithUserInfo(projectId)).thenReturn(members);

        // When
        ResponseEntity<List<ProjectMemberDTO>> response = projectController.getProjectMembers(projectId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(projectService, times(1)).getProjectMembersWithUserInfo(projectId);
    }

    @Test
    void testCreateProject() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        when(projectService.save(any(Project.class), any(UUID.class))).thenReturn(testProject);
        
        // Mock SecurityUtil.getCurrentUserId() using MockedStatic
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(currentUserId);

            // When
            ResponseEntity<Project> response = projectController.createProject(testProject);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testProject, response.getBody());
            verify(projectService, times(1)).save(any(Project.class), eq(currentUserId));
        }
    }

    @Test
    void testCreateProject_WhenUserNotAuthenticated() {
        // Given
        // Mock SecurityUtil.getCurrentUserId() to return null
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(null);

            // When
            ResponseEntity<Project> response = projectController.createProject(testProject);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNull(response.getBody());
            verify(projectService, never()).save(any(Project.class), any(UUID.class));
        }
    }

    @Test
    void testUpdateProject_WhenProjectExists() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        Project updatedProject = new Project();
        updatedProject.setId(projectId);
        updatedProject.setName("Updated Project");
        when(projectService.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectService.save(any(Project.class), any(UUID.class))).thenReturn(updatedProject);
        
        // Mock SecurityUtil.getCurrentUserId()
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(currentUserId);

            // When
            ResponseEntity<Project> response = projectController.updateProject(projectId, updatedProject);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Updated Project", response.getBody().getName());
            verify(projectService, times(1)).findById(projectId);
            verify(projectService, times(1)).save(any(Project.class), eq(currentUserId));
        }
    }

    @Test
    void testUpdateProject_WhenProjectDoesNotExist() {
        // Given
        when(projectService.findById(projectId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Project> response = projectController.updateProject(projectId, testProject);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).findById(projectId);
        verify(projectService, never()).save(any(Project.class), any(UUID.class));
    }

    @Test
    void testDeleteProject_WhenProjectExists() {
        // Given
        when(projectService.findById(projectId)).thenReturn(Optional.of(testProject));
        doNothing().when(projectService).deleteById(projectId);

        // When
        ResponseEntity<Void> response = projectController.deleteProject(projectId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).findById(projectId);
        verify(projectService, times(1)).deleteById(projectId);
    }

    @Test
    void testDeleteProject_WhenProjectDoesNotExist() {
        // Given
        when(projectService.findById(projectId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = projectController.deleteProject(projectId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(projectService, times(1)).findById(projectId);
        verify(projectService, never()).deleteById(projectId);
    }

    @Test
    void testInviteMember_Success() {
        // Given
        UUID inviterId = UUID.randomUUID();
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("newuser@example.com");
        request.setRole(Role.MEMBER);
        when(projectService.inviteMemberByEmail(any(UUID.class), any(InviteMemberRequest.class), any(UUID.class)))
                .thenReturn(testProject);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(inviterId);

            // When
            ResponseEntity<?> response = projectController.inviteMember(projectId, request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(projectService, times(1)).inviteMemberByEmail(any(UUID.class), any(InviteMemberRequest.class), eq(inviterId));
        }
    }

    @Test
    void testInviteMember_WhenServiceThrowsException() {
        // Given
        UUID inviterId = UUID.randomUUID();
        InviteMemberRequest request = new InviteMemberRequest();
        request.setEmail("newuser@example.com");
        request.setRole(Role.MEMBER);
        when(projectService.inviteMemberByEmail(any(UUID.class), any(InviteMemberRequest.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Project not found"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(inviterId);

            // When
            ResponseEntity<?> response = projectController.inviteMember(projectId, request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Project not found", response.getBody());
            verify(projectService, times(1)).inviteMemberByEmail(any(UUID.class), any(InviteMemberRequest.class), eq(inviterId));
        }
    }

    @Test
    void testUpdateMemberRole_Success() {
        // Given
        UUID modifierId = UUID.randomUUID();
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest();
        request.setRole(Role.ADMIN);
        when(projectService.updateMemberRole(any(UUID.class), any(UUID.class), any(UpdateMemberRoleRequest.class), any(UUID.class)))
                .thenReturn(testProject);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(modifierId);

            // When
            ResponseEntity<?> response = projectController.updateMemberRole(projectId, userId, request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(projectService, times(1)).updateMemberRole(any(UUID.class), any(UUID.class), any(UpdateMemberRoleRequest.class), eq(modifierId));
        }
    }

    @Test
    void testUpdateMemberRole_WhenServiceThrowsException() {
        // Given
        UUID modifierId = UUID.randomUUID();
        UpdateMemberRoleRequest request = new UpdateMemberRoleRequest();
        request.setRole(Role.ADMIN);
        when(projectService.updateMemberRole(any(UUID.class), any(UUID.class), any(UpdateMemberRoleRequest.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Member not found"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(modifierId);

            // When
            ResponseEntity<?> response = projectController.updateMemberRole(projectId, userId, request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Member not found", response.getBody());
            verify(projectService, times(1)).updateMemberRole(any(UUID.class), any(UUID.class), any(UpdateMemberRoleRequest.class), eq(modifierId));
        }
    }

    @Test
    void testRemoveMember_Success() {
        // Given
        when(projectService.removeMember(any(UUID.class), any(UUID.class))).thenReturn(testProject);

        // When
        ResponseEntity<Project> response = projectController.removeMember(projectId, userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(projectService, times(1)).removeMember(projectId, userId);
    }

    @Test
    void testRemoveMember_WhenServiceThrowsException() {
        // Given
        when(projectService.removeMember(any(UUID.class), any(UUID.class)))
                .thenThrow(new RuntimeException("Member not found"));

        // When
        ResponseEntity<Project> response = projectController.removeMember(projectId, userId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(projectService, times(1)).removeMember(projectId, userId);
    }
}
