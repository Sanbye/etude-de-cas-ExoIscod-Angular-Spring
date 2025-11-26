package com.codeSolution.PMT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codeSolution.PMT.model.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("SELECT t FROM Task t WHERE t.projectMember.projectId = :projectId")
    List<Task> findByProjectId(@Param("projectId") UUID projectId);
    
    @Query("SELECT t FROM Task t WHERE t.projectMember.userId = :userId")
    List<Task> findByAssignedUserId(@Param("userId") UUID userId);
    
    @Query("SELECT t FROM Task t WHERE t.projectMember.projectId = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") UUID projectId, @Param("status") Task.TaskStatus status);
}

