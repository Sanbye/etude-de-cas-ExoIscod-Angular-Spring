package com.codeSolution.PMT.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_history")
@Data
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "project_member_project_id", referencedColumnName = "project_id", nullable = false),
        @JoinColumn(name = "project_member_user_id", referencedColumnName = "user_id", nullable = false)
    })
    @JsonIgnore
    private ProjectMember projectMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_name", nullable = false)
    private FieldName fieldName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        modifiedAt = LocalDateTime.now();
    }

    public enum FieldName {
        name,
        description,
        dueDate,
        priority,
        status,
        endDate
    }
}

