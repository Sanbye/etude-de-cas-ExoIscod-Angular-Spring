package com.codeSolution.PMT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codeSolution.PMT.model.Project;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE pm.userId = :userId")
    List<Project> findByMemberId(@Param("userId") UUID userId);
}

