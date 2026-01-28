package com.codeSolution.PMT.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserIdReturnsNullWhenNoAuthentication() {
        SecurityContextHolder.clearContext();
        assertNull(SecurityUtil.getCurrentUserId());
        assertFalse(SecurityUtil.isAuthenticated());
    }

    @Test
    void getCurrentUserIdReturnsUuidWhenAuthenticated() {
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals(userId, SecurityUtil.getCurrentUserId());
        assertTrue(SecurityUtil.isAuthenticated());
    }

    @Test
    void getCurrentUserIdReturnsNullWhenPrincipalNotUuid() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("not-uuid", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertNull(SecurityUtil.getCurrentUserId());
        assertFalse(SecurityUtil.isAuthenticated());
    }

    @Test
    void isAuthenticatedReturnsFalseWhenNotAuthenticated() {
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null);
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals(userId, SecurityUtil.getCurrentUserId());
        assertFalse(SecurityUtil.isAuthenticated());
    }
}
