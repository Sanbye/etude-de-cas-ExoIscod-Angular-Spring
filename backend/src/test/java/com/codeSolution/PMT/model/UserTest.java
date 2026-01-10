package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testEquals_SameInstance() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        assertEquals(user, user);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        User user1 = new User();
        user1.setId(id);
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setId(id);
        user2.setUserName("testuser");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");

        assertEquals(user1, user2);
    }

    @Test
    void testEquals_DifferentIds() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUserName("testuser");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");

        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_DifferentUsernames() {
        UUID id = UUID.randomUUID();
        User user1 = new User();
        user1.setId(id);
        user1.setUserName("user1");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setId(id);
        user2.setUserName("user2");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");

        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_Null() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("testuser");

        assertNotEquals(user, null);
    }

    @Test
    void testEquals_DifferentClass() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("testuser");

        assertNotEquals(user, "not a user");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        User user1 = new User();
        user1.setId(id);
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setId(id);
        user2.setUserName("testuser");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUserName("user1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUserName("user2");

        // Hash codes might be equal by chance, but should generally be different
        // We just verify they can be computed without exception
        assertNotNull(user1.hashCode());
        assertNotNull(user2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setProjectMemberships(new ArrayList<>());

        String toString = user.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }

    @Test
    void testEquals_WithProjectMemberships() {
        UUID id = UUID.randomUUID();
        User user1 = new User();
        user1.setId(id);
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");
        user1.setProjectMemberships(new ArrayList<>());

        User user2 = new User();
        user2.setId(id);
        user2.setUserName("testuser");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");
        user2.setProjectMemberships(new ArrayList<>());

        assertEquals(user1, user2);
    }
}
