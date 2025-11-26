package com.codeSolution.PMT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeSolution.PMT.model.TaskHistory;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, UUID> {
    List<TaskHistory> findByTaskIdOrderByModifiedAtDesc(UUID taskId);
}

