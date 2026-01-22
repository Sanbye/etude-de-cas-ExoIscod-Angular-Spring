package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberTest {

    @Test
    void testEquals_SameInstance() {
        ProjectMember member = new ProjectMember();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(Role.MEMBER);

        assertEquals(member, member);
    }

    @Test
    void testEquals_SameValues() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(projectId);
        member1.setUserId(userId);
        member1.setRole(Role.MEMBER);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(projectId);
        member2.setUserId(userId);
        member2.setRole(Role.MEMBER);

        assertEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentProjectIds() {
        UUID userId = UUID.randomUUID();

        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(UUID.randomUUID());
        member1.setUserId(userId);
        member1.setRole(Role.MEMBER);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(UUID.randomUUID());
        member2.setUserId(userId);
        member2.setRole(Role.MEMBER);

        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentUserIds() {
        UUID projectId = UUID.randomUUID();

        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(projectId);
        member1.setUserId(UUID.randomUUID());
        member1.setRole(Role.MEMBER);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(projectId);
        member2.setUserId(UUID.randomUUID());
        member2.setRole(Role.MEMBER);

        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentRoles() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(projectId);
        member1.setUserId(userId);
        member1.setRole(Role.ADMIN);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(projectId);
        member2.setUserId(userId);
        member2.setRole(Role.MEMBER);

        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_Null() {
        ProjectMember member = new ProjectMember();
        member.setProjectId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setRole(Role.MEMBER);

        assertNotEquals(member, null);
    }

    @Test
    void testEquals_DifferentClass() {
        ProjectMember member = new ProjectMember();
        member.setProjectId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setRole(Role.MEMBER);

        assertNotEquals(member, "not a project member");
    }

    @Test
    void testHashCode_SameValues() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(projectId);
        member1.setUserId(userId);
        member1.setRole(Role.MEMBER);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(projectId);
        member2.setUserId(userId);
        member2.setRole(Role.MEMBER);

        assertEquals(member1.hashCode(), member2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        ProjectMember member1 = new ProjectMember();
        member1.setProjectId(UUID.randomUUID());
        member1.setUserId(UUID.randomUUID());
        member1.setRole(Role.ADMIN);

        ProjectMember member2 = new ProjectMember();
        member2.setProjectId(UUID.randomUUID());
        member2.setUserId(UUID.randomUUID());
        member2.setRole(Role.MEMBER);

        assertNotNull(member1.hashCode());
        assertNotNull(member2.hashCode());
    }

    @Test
    void testToString() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(Role.ADMIN);

        String toString = member.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("ProjectMember"));
    }
}
