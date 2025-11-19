package com.codeSolution.PNT.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendTaskAssignmentNotification(String userEmail, String taskTitle, String projectName) {
        // Pour l'instant, on log simplement. Dans un vrai projet, on utiliserait Spring Mail
        log.info("Email de notification envoyé à {} : Tâche '{}' assignée dans le projet '{}'", 
                userEmail, taskTitle, projectName);
        // TODO: Implémenter l'envoi d'email réel avec Spring Mail
    }

    public void sendProjectInvitation(String userEmail, String projectName, String inviterName) {
        log.info("Email d'invitation envoyé à {} : Invitation au projet '{}' par {}", 
                userEmail, projectName, inviterName);
        // TODO: Implémenter l'envoi d'email réel avec Spring Mail
    }
}

