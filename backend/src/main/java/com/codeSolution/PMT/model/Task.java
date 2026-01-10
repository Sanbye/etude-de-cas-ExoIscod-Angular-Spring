package com.codeSolution.PMT.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "project_member_project_id", referencedColumnName = "project_id", nullable = false),
        @JoinColumn(name = "project_member_user_id", referencedColumnName = "user_id", nullable = false)
    })
    @JsonIgnore
    private ProjectMember projectMember;

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }
}

