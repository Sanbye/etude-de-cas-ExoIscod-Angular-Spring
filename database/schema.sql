-- Script de création de la base de données
-- Base de données: project_management

-- Extension pour UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Suppression des tables si elles existent (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS task_history CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS project_members CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- Table des rôles
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT chk_role_name CHECK (name IN ('ADMIN', 'MEMBER', 'OBSERVER'))
);

-- Table des utilisateurs
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Table des projets
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    starting_date DATE
);

-- Table de liaison entre projets et membres avec rôles
CREATE TABLE project_members (
    project_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_member_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_member_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Table des tâches
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_member_project_id UUID NOT NULL,
    project_member_user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    end_date DATE,
    CONSTRAINT fk_task_project_member FOREIGN KEY (project_member_project_id, project_member_user_id) 
        REFERENCES project_members(project_id, user_id) ON DELETE CASCADE,
    CONSTRAINT chk_task_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    CONSTRAINT chk_task_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Table de l'historique des modifications des tâches
CREATE TABLE task_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id UUID NOT NULL,
    project_member_project_id UUID NOT NULL,
    project_member_user_id UUID NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    field_name VARCHAR(20) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    CONSTRAINT fk_task_history_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_history_project_member FOREIGN KEY (project_member_project_id, project_member_user_id) 
        REFERENCES project_members(project_id, user_id) ON DELETE CASCADE,
    CONSTRAINT chk_task_history_field_name CHECK (field_name IN ('name', 'description', 'dueDate', 'priority', 'status', 'endDate'))
);

-- Table des notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_member_project_id UUID NOT NULL,
    project_member_user_id UUID NOT NULL,
    task_id UUID NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_notification_project_member FOREIGN KEY (project_member_project_id, project_member_user_id) 
        REFERENCES project_members(project_id, user_id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_project_member_user ON project_members(user_id);
CREATE INDEX idx_project_member_role ON project_members(role_id);
CREATE INDEX idx_task_project_member ON tasks(project_member_project_id, project_member_user_id);
CREATE INDEX idx_task_status ON tasks(status);
CREATE INDEX idx_task_history_task ON task_history(task_id);
CREATE INDEX idx_task_history_project_member ON task_history(project_member_project_id, project_member_user_id);
CREATE INDEX idx_notification_project_member ON notifications(project_member_project_id, project_member_user_id);
CREATE INDEX idx_notification_task ON notifications(task_id);
CREATE INDEX idx_notification_is_read ON notifications(is_read);
