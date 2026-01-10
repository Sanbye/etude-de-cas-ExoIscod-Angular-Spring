package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testEquals_SameInstance() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setMessage("Test message");

        assertEquals(notification, notification);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        Notification notification1 = new Notification();
        notification1.setId(id);
        notification1.setMessage("Test message");
        notification1.setCreatedAt(createdAt);
        notification1.setIsRead(false);

        Notification notification2 = new Notification();
        notification2.setId(id);
        notification2.setMessage("Test message");
        notification2.setCreatedAt(createdAt);
        notification2.setIsRead(false);

        assertEquals(notification1, notification2);
    }

    @Test
    void testEquals_DifferentIds() {
        Notification notification1 = new Notification();
        notification1.setId(UUID.randomUUID());
        notification1.setMessage("Test message");

        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setMessage("Test message");

        assertNotEquals(notification1, notification2);
    }

    @Test
    void testEquals_DifferentMessages() {
        UUID id = UUID.randomUUID();
        Notification notification1 = new Notification();
        notification1.setId(id);
        notification1.setMessage("Message 1");

        Notification notification2 = new Notification();
        notification2.setId(id);
        notification2.setMessage("Message 2");

        assertNotEquals(notification1, notification2);
    }

    @Test
    void testEquals_Null() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setMessage("Test message");

        assertNotEquals(notification, null);
    }

    @Test
    void testEquals_DifferentClass() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setMessage("Test message");

        assertNotEquals(notification, "not a notification");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        Notification notification1 = new Notification();
        notification1.setId(id);
        notification1.setMessage("Test message");
        notification1.setCreatedAt(createdAt);
        notification1.setIsRead(false);

        Notification notification2 = new Notification();
        notification2.setId(id);
        notification2.setMessage("Test message");
        notification2.setCreatedAt(createdAt);
        notification2.setIsRead(false);

        assertEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        Notification notification1 = new Notification();
        notification1.setId(UUID.randomUUID());
        notification1.setMessage("Message 1");

        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setMessage("Message 2");

        assertNotNull(notification1.hashCode());
        assertNotNull(notification2.hashCode());
    }

    @Test
    void testToString() {
        Notification notification = new Notification();
        UUID id = UUID.randomUUID();
        notification.setId(id);
        notification.setMessage("Test notification message");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        String toString = notification.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Notification"));
        assertTrue(toString.contains("Test notification message"));
    }

    @Test
    void testIsRead_DefaultValue() {
        Notification notification = new Notification();
        assertFalse(notification.getIsRead());
    }
}
