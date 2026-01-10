package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals_SameInstance() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setName("Test Task");

        assertEquals(task, task);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        Task task1 = new Task();
        task1.setId(id);
        task1.setName("Test Task");
        task1.setDescription("Test Description");
        task1.setStatus(Task.TaskStatus.TODO);
        task1.setPriority(Task.TaskPriority.MEDIUM);

        Task task2 = new Task();
        task2.setId(id);
        task2.setName("Test Task");
        task2.setDescription("Test Description");
        task2.setStatus(Task.TaskStatus.TODO);
        task2.setPriority(Task.TaskPriority.MEDIUM);

        assertEquals(task1, task2);
    }

    @Test
    void testEquals_DifferentIds() {
        Task task1 = new Task();
        task1.setId(UUID.randomUUID());
        task1.setName("Test Task");

        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setName("Test Task");

        assertNotEquals(task1, task2);
    }

    @Test
    void testEquals_DifferentStatuses() {
        UUID id = UUID.randomUUID();
        Task task1 = new Task();
        task1.setId(id);
        task1.setName("Test Task");
        task1.setStatus(Task.TaskStatus.TODO);

        Task task2 = new Task();
        task2.setId(id);
        task2.setName("Test Task");
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);

        assertNotEquals(task1, task2);
    }

    @Test
    void testEquals_DifferentPriorities() {
        UUID id = UUID.randomUUID();
        Task task1 = new Task();
        task1.setId(id);
        task1.setName("Test Task");
        task1.setPriority(Task.TaskPriority.LOW);

        Task task2 = new Task();
        task2.setId(id);
        task2.setName("Test Task");
        task2.setPriority(Task.TaskPriority.HIGH);

        assertNotEquals(task1, task2);
    }

    @Test
    void testEquals_Null() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setName("Test Task");

        assertNotEquals(task, null);
    }

    @Test
    void testEquals_DifferentClass() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setName("Test Task");

        assertNotEquals(task, "not a task");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        Task task1 = new Task();
        task1.setId(id);
        task1.setName("Test Task");
        task1.setStatus(Task.TaskStatus.TODO);
        task1.setPriority(Task.TaskPriority.MEDIUM);

        Task task2 = new Task();
        task2.setId(id);
        task2.setName("Test Task");
        task2.setStatus(Task.TaskStatus.TODO);
        task2.setPriority(Task.TaskPriority.MEDIUM);

        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        Task task1 = new Task();
        task1.setId(UUID.randomUUID());
        task1.setName("Task 1");

        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setName("Task 2");

        assertNotNull(task1.hashCode());
        assertNotNull(task2.hashCode());
    }

    @Test
    void testToString() {
        Task task = new Task();
        UUID id = UUID.randomUUID();
        task.setId(id);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Task.TaskStatus.TODO);
        task.setPriority(Task.TaskPriority.HIGH);
        task.setDueDate(LocalDate.of(2024, 12, 31));

        String toString = task.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Task"));
        assertTrue(toString.contains("Test Task"));
        assertTrue(toString.contains("Test Description"));
    }

    @Test
    void testTaskStatusEnum() {
        assertEquals(Task.TaskStatus.TODO, Task.TaskStatus.valueOf("TODO"));
        assertEquals(Task.TaskStatus.IN_PROGRESS, Task.TaskStatus.valueOf("IN_PROGRESS"));
        assertEquals(Task.TaskStatus.DONE, Task.TaskStatus.valueOf("DONE"));
    }

    @Test
    void testTaskPriorityEnum() {
        assertEquals(Task.TaskPriority.LOW, Task.TaskPriority.valueOf("LOW"));
        assertEquals(Task.TaskPriority.MEDIUM, Task.TaskPriority.valueOf("MEDIUM"));
        assertEquals(Task.TaskPriority.HIGH, Task.TaskPriority.valueOf("HIGH"));
    }
}
