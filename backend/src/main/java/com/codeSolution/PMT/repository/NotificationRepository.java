package com.codeSolution.PMT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeSolution.PMT.model.Notification;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByProjectMemberProjectIdAndProjectMemberUserId(UUID projectId, UUID userId);
    List<Notification> findByProjectMemberProjectIdAndProjectMemberUserIdAndIsRead(UUID projectId, UUID userId, Boolean isRead);
    List<Notification> findByTaskId(UUID taskId);
}

