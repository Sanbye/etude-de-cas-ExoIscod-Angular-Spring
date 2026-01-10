package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberIdTest {

    @Test
    void testEquals_SameInstance() {
        ProjectMemberId id = new ProjectMemberId();
        id.setProjectId(UUID.randomUUID());
        id.setUserId(UUID.randomUUID());

        assertEquals(id, id);
    }

    @Test
    void testEquals_SameValues() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMemberId id1 = new ProjectMemberId();
        id1.setProjectId(projectId);
        id1.setUserId(userId);

        ProjectMemberId id2 = new ProjectMemberId();
        id2.setProjectId(projectId);
        id2.setUserId(userId);

        assertEquals(id1, id2);
    }

    @Test
    void testEquals_DifferentProjectIds() {
        UUID userId = UUID.randomUUID();

        ProjectMemberId id1 = new ProjectMemberId();
        id1.setProjectId(UUID.randomUUID());
        id1.setUserId(userId);

        ProjectMemberId id2 = new ProjectMemberId();
        id2.setProjectId(UUID.randomUUID());
        id2.setUserId(userId);

        assertNotEquals(id1, id2);
    }

    @Test
    void testEquals_DifferentUserIds() {
        UUID projectId = UUID.randomUUID();

        ProjectMemberId id1 = new ProjectMemberId();
        id1.setProjectId(projectId);
        id1.setUserId(UUID.randomUUID());

        ProjectMemberId id2 = new ProjectMemberId();
        id2.setProjectId(projectId);
        id2.setUserId(UUID.randomUUID());

        assertNotEquals(id1, id2);
    }

    @Test
    void testEquals_Null() {
        ProjectMemberId id = new ProjectMemberId();
        id.setProjectId(UUID.randomUUID());
        id.setUserId(UUID.randomUUID());

        assertNotEquals(id, null);
    }

    @Test
    void testEquals_DifferentClass() {
        ProjectMemberId id = new ProjectMemberId();
        id.setProjectId(UUID.randomUUID());
        id.setUserId(UUID.randomUUID());

        assertNotEquals(id, "not a project member id");
    }

    @Test
    void testHashCode_SameValues() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectMemberId id1 = new ProjectMemberId();
        id1.setProjectId(projectId);
        id1.setUserId(userId);

        ProjectMemberId id2 = new ProjectMemberId();
        id2.setProjectId(projectId);
        id2.setUserId(userId);

        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        ProjectMemberId id1 = new ProjectMemberId();
        id1.setProjectId(UUID.randomUUID());
        id1.setUserId(UUID.randomUUID());

        ProjectMemberId id2 = new ProjectMemberId();
        id2.setProjectId(UUID.randomUUID());
        id2.setUserId(UUID.randomUUID());

        assertNotNull(id1.hashCode());
        assertNotNull(id2.hashCode());
    }

    @Test
    void testToString() {
        ProjectMemberId id = new ProjectMemberId();
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        id.setProjectId(projectId);
        id.setUserId(userId);

        String toString = id.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("ProjectMemberId"));
    }
}
