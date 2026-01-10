package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testEquals_SameInstance() {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Test Project");

        assertEquals(project, project);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        Project project1 = new Project();
        project1.setId(id);
        project1.setName("Test Project");
        project1.setDescription("Test Description");
        project1.setStartingDate(LocalDate.now());

        Project project2 = new Project();
        project2.setId(id);
        project2.setName("Test Project");
        project2.setDescription("Test Description");
        project2.setStartingDate(LocalDate.now());

        assertEquals(project1, project2);
    }

    @Test
    void testEquals_DifferentIds() {
        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Test Project");

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Test Project");

        assertNotEquals(project1, project2);
    }

    @Test
    void testEquals_DifferentNames() {
        UUID id = UUID.randomUUID();
        Project project1 = new Project();
        project1.setId(id);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(id);
        project2.setName("Project 2");

        assertNotEquals(project1, project2);
    }

    @Test
    void testEquals_Null() {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Test Project");

        assertNotEquals(project, null);
    }

    @Test
    void testEquals_DifferentClass() {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Test Project");

        assertNotEquals(project, "not a project");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        Project project1 = new Project();
        project1.setId(id);
        project1.setName("Test Project");
        project1.setDescription("Test Description");

        Project project2 = new Project();
        project2.setId(id);
        project2.setName("Test Project");
        project2.setDescription("Test Description");

        assertEquals(project1.hashCode(), project2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Project 2");

        assertNotNull(project1.hashCode());
        assertNotNull(project2.hashCode());
    }

    @Test
    void testToString() {
        Project project = new Project();
        UUID id = UUID.randomUUID();
        project.setId(id);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartingDate(LocalDate.of(2024, 1, 1));
        project.setProjectMembers(new ArrayList<>());

        String toString = project.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Project"));
        assertTrue(toString.contains("Test Project"));
        assertTrue(toString.contains("Test Description"));
    }

    @Test
    void testEquals_WithProjectMembers() {
        UUID id = UUID.randomUUID();
        Project project1 = new Project();
        project1.setId(id);
        project1.setName("Test Project");
        project1.setProjectMembers(new ArrayList<>());

        Project project2 = new Project();
        project2.setId(id);
        project2.setName("Test Project");
        project2.setProjectMembers(new ArrayList<>());

        assertEquals(project1, project2);
    }
}
