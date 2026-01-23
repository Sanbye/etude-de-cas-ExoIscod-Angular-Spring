package com.codeSolution.PMT.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Data
    public static class EmailNotificationResult {
        private final String userEmail;
        private final String taskTitle;
        private final String projectName;
        private final boolean sent;
    }

    public EmailNotificationResult sendTaskAssignmentNotification(String userEmail, String taskTitle, String projectName) {
        log.info("Email de notification envoyé à {} : Tâche '{}' assignée dans le projet '{}'", 
                userEmail, taskTitle, projectName);
        return new EmailNotificationResult(userEmail, taskTitle, projectName, true);
    }

    public void sendProjectInvitation(String userEmail, String projectName, String inviterName) {
        log.info("Email d'invitation envoyé à {} : Invitation au projet '{}' par {}", 
                userEmail, projectName, inviterName);
    }
}

