package com.codeSolution.PMT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeSolution.PMT.model.ProjectMember;
import com.codeSolution.PMT.model.ProjectMemberId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMember> findByProjectId(UUID projectId);
    List<ProjectMember> findByUserId(UUID userId);
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
}

