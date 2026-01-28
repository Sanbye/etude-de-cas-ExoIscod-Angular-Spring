package com.codeSolution.PMT.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Test
    void testEmailNotificationResultEqualsAndHashCode() {
        EmailService.EmailNotificationResult base =
                new EmailService.EmailNotificationResult("a@example.com", "Task A", "Project A", true);
        EmailService.EmailNotificationResult same =
                new EmailService.EmailNotificationResult("a@example.com", "Task A", "Project A", true);
        EmailService.EmailNotificationResult diffEmail =
                new EmailService.EmailNotificationResult("b@example.com", "Task A", "Project A", true);
        EmailService.EmailNotificationResult diffTask =
                new EmailService.EmailNotificationResult("a@example.com", "Task B", "Project A", true);
        EmailService.EmailNotificationResult diffProject =
                new EmailService.EmailNotificationResult("a@example.com", "Task A", "Project B", true);
        EmailService.EmailNotificationResult diffSent =
                new EmailService.EmailNotificationResult("a@example.com", "Task A", "Project A", false);
        EmailService.EmailNotificationResult allNull =
                new EmailService.EmailNotificationResult(null, null, null, false);
        EmailService.EmailNotificationResult allNullCopy =
                new EmailService.EmailNotificationResult(null, null, null, false);

        assertEquals(base, base);
        assertEquals(base, same);
        assertNotEquals(base, diffEmail);
        assertNotEquals(base, diffTask);
        assertNotEquals(base, diffProject);
        assertNotEquals(base, diffSent);
        assertNotEquals(base, null);
        assertNotEquals(base, "not-a-result");
        assertEquals(allNull, allNullCopy);

        assertEquals(base.hashCode(), same.hashCode());
        assertEquals(allNull.hashCode(), allNullCopy.hashCode());
    }
}
