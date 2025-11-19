package com.codeSolution.PNT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codeSolution.PNT.model.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE pm.userId = :userId")
    List<Project> findByMemberId(@Param("userId") Long userId);
}

