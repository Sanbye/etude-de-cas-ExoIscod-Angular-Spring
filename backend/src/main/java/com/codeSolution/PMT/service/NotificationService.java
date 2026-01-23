package com.codeSolution.PMT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeSolution.PMT.model.Notification;
import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.Task;
import com.codeSolution.PMT.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createTaskAssignmentNotification(ProjectMember assigneeMember, Task task) {
        Notification notification = new Notification();
        notification.setProjectMember(assigneeMember);
        notification.setTask(task);
        notification.setMessage(String.format("La tâche '%s' vous a été assignée dans le projet '%s'.", 
                task.getName(), assigneeMember.getProject().getName()));
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
}
