package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskHistoryTest {

    @Test
    void testEquals_SameInstance() {
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        history.setFieldName(TaskHistory.FieldName.name);

        assertEquals(history, history);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime modifiedAt = LocalDateTime.now();

        TaskHistory history1 = new TaskHistory();
        history1.setId(id);
        history1.setFieldName(TaskHistory.FieldName.name);
        history1.setOldValue("Old Value");
        history1.setNewValue("New Value");
        history1.setModifiedAt(modifiedAt);

        TaskHistory history2 = new TaskHistory();
        history2.setId(id);
        history2.setFieldName(TaskHistory.FieldName.name);
        history2.setOldValue("Old Value");
        history2.setNewValue("New Value");
        history2.setModifiedAt(modifiedAt);

        assertEquals(history1, history2);
    }

    @Test
    void testEquals_DifferentIds() {
        TaskHistory history1 = new TaskHistory();
        history1.setId(UUID.randomUUID());
        history1.setFieldName(TaskHistory.FieldName.name);

        TaskHistory history2 = new TaskHistory();
        history2.setId(UUID.randomUUID());
        history2.setFieldName(TaskHistory.FieldName.name);

        assertNotEquals(history1, history2);
    }

    @Test
    void testEquals_DifferentFieldNames() {
        UUID id = UUID.randomUUID();
        TaskHistory history1 = new TaskHistory();
        history1.setId(id);
        history1.setFieldName(TaskHistory.FieldName.name);

        TaskHistory history2 = new TaskHistory();
        history2.setId(id);
        history2.setFieldName(TaskHistory.FieldName.status);

        assertNotEquals(history1, history2);
    }

    @Test
    void testEquals_Null() {
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        history.setFieldName(TaskHistory.FieldName.name);

        assertNotEquals(history, null);
    }

    @Test
    void testEquals_DifferentClass() {
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        history.setFieldName(TaskHistory.FieldName.name);

        assertNotEquals(history, "not a task history");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime modifiedAt = LocalDateTime.now();

        TaskHistory history1 = new TaskHistory();
        history1.setId(id);
        history1.setFieldName(TaskHistory.FieldName.name);
        history1.setOldValue("Old Value");
        history1.setNewValue("New Value");
        history1.setModifiedAt(modifiedAt);

        TaskHistory history2 = new TaskHistory();
        history2.setId(id);
        history2.setFieldName(TaskHistory.FieldName.name);
        history2.setOldValue("Old Value");
        history2.setNewValue("New Value");
        history2.setModifiedAt(modifiedAt);

        assertEquals(history1.hashCode(), history2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        TaskHistory history1 = new TaskHistory();
        history1.setId(UUID.randomUUID());
        history1.setFieldName(TaskHistory.FieldName.name);

        TaskHistory history2 = new TaskHistory();
        history2.setId(UUID.randomUUID());
        history2.setFieldName(TaskHistory.FieldName.status);

        assertNotNull(history1.hashCode());
        assertNotNull(history2.hashCode());
    }

    @Test
    void testToString() {
        TaskHistory history = new TaskHistory();
        UUID id = UUID.randomUUID();
        history.setId(id);
        history.setFieldName(TaskHistory.FieldName.name);
        history.setOldValue("Old Name");
        history.setNewValue("New Name");
        history.setModifiedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        String toString = history.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("TaskHistory"));
    }

    @Test
    void testFieldNameEnum() {
        assertEquals(TaskHistory.FieldName.name, TaskHistory.FieldName.valueOf("name"));
        assertEquals(TaskHistory.FieldName.description, TaskHistory.FieldName.valueOf("description"));
        assertEquals(TaskHistory.FieldName.dueDate, TaskHistory.FieldName.valueOf("dueDate"));
        assertEquals(TaskHistory.FieldName.priority, TaskHistory.FieldName.valueOf("priority"));
        assertEquals(TaskHistory.FieldName.status, TaskHistory.FieldName.valueOf("status"));
        assertEquals(TaskHistory.FieldName.endDate, TaskHistory.FieldName.valueOf("endDate"));
    }
}
