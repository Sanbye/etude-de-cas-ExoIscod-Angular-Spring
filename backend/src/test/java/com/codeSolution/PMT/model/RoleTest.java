package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testEquals_SameInstance() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(Role.RoleName.ADMIN);

        assertEquals(role, role);
    }

    @Test
    void testEquals_SameValues() {
        UUID id = UUID.randomUUID();
        Role role1 = new Role();
        role1.setId(id);
        role1.setName(Role.RoleName.MEMBER);

        Role role2 = new Role();
        role2.setId(id);
        role2.setName(Role.RoleName.MEMBER);

        assertEquals(role1, role2);
    }

    @Test
    void testEquals_DifferentIds() {
        Role role1 = new Role();
        role1.setId(UUID.randomUUID());
        role1.setName(Role.RoleName.MEMBER);

        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setName(Role.RoleName.MEMBER);

        assertNotEquals(role1, role2);
    }

    @Test
    void testEquals_DifferentNames() {
        UUID id = UUID.randomUUID();
        Role role1 = new Role();
        role1.setId(id);
        role1.setName(Role.RoleName.ADMIN);

        Role role2 = new Role();
        role2.setId(id);
        role2.setName(Role.RoleName.MEMBER);

        assertNotEquals(role1, role2);
    }

    @Test
    void testEquals_Null() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(Role.RoleName.MEMBER);

        assertNotEquals(role, null);
    }

    @Test
    void testEquals_DifferentClass() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(Role.RoleName.MEMBER);

        assertNotEquals(role, "not a role");
    }

    @Test
    void testHashCode_SameValues() {
        UUID id = UUID.randomUUID();
        Role role1 = new Role();
        role1.setId(id);
        role1.setName(Role.RoleName.MEMBER);

        Role role2 = new Role();
        role2.setId(id);
        role2.setName(Role.RoleName.MEMBER);

        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues() {
        Role role1 = new Role();
        role1.setId(UUID.randomUUID());
        role1.setName(Role.RoleName.ADMIN);

        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setName(Role.RoleName.MEMBER);

        assertNotNull(role1.hashCode());
        assertNotNull(role2.hashCode());
    }

    @Test
    void testToString() {
        Role role = new Role();
        UUID id = UUID.randomUUID();
        role.setId(id);
        role.setName(Role.RoleName.ADMIN);

        String toString = role.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Role"));
    }

    @Test
    void testRoleNameEnum() {
        assertEquals(Role.RoleName.ADMIN, Role.RoleName.valueOf("ADMIN"));
        assertEquals(Role.RoleName.MEMBER, Role.RoleName.valueOf("MEMBER"));
        assertEquals(Role.RoleName.OBSERVER, Role.RoleName.valueOf("OBSERVER"));
    }
}
