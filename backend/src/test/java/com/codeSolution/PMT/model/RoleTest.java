package com.codeSolution.PMT.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testEnumValues() {
        // Vérifier que tous les rôles existent
        assertNotNull(Role.ADMIN);
        assertNotNull(Role.MEMBER);
        assertNotNull(Role.OBSERVER);
    }

    @Test
    void testValueOf() {
        // Vérifier que valueOf fonctionne correctement
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.MEMBER, Role.valueOf("MEMBER"));
        assertEquals(Role.OBSERVER, Role.valueOf("OBSERVER"));
    }

    @Test
    void testValueOf_InvalidValue() {
        // Vérifier qu'une valeur invalide lève une exception
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("INVALID");
        });
    }

    @Test
    void testValues() {
        // Vérifier que values() retourne tous les rôles
        Role[] values = Role.values();
        assertEquals(3, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(Role.ADMIN));
        assertTrue(java.util.Arrays.asList(values).contains(Role.MEMBER));
        assertTrue(java.util.Arrays.asList(values).contains(Role.OBSERVER));
    }

    @Test
    void testName() {
        // Vérifier que name() retourne le nom correct
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("MEMBER", Role.MEMBER.name());
        assertEquals("OBSERVER", Role.OBSERVER.name());
    }

    @Test
    void testOrdinal() {
        // Vérifier que ordinal() retourne la position correcte
        assertEquals(0, Role.ADMIN.ordinal());
        assertEquals(1, Role.MEMBER.ordinal());
        assertEquals(2, Role.OBSERVER.ordinal());
    }
}
