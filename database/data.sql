-- Script d'insertion des données de test
-- Mot de passe par défaut pour tous les utilisateurs : "password123"

-- Insertion d'utilisateurs de test
INSERT INTO users (username, email, first_name, last_name, password, created_at, updated_at) VALUES
('john.doe', 'john.doe@example.com', 'John', 'Doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', 'Jane', 'Smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP),
('bob.johnson', 'bob.johnson@example.com', 'Bob', 'Johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP),
('alice.brown', 'alice.brown@example.com', 'Alice', 'Brown', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP),
('charlie.wilson', 'charlie.wilson@example.com', 'Charlie', 'Wilson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP);

-- Insertion de projets de test avec dates de début
INSERT INTO projects (name, description, status, start_date, owner_id, created_at, updated_at) VALUES
('Application Web E-commerce', 'Développement d''une application web e-commerce avec Angular et Spring Boot', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '20 days', 1, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP),
('Système de Gestion de Projets', 'Application de gestion de projets avec suivi des tâches', 'PLANNED', CURRENT_TIMESTAMP + INTERVAL '5 days', 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
('API REST pour Mobile', 'Développement d''une API REST pour application mobile', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '15 days', 1, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP),
('Site Vitrine Entreprise', 'Création d''un site vitrine pour présenter l''entreprise', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '40 days', 3, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('Application de Gestion RH', 'Système de gestion des ressources humaines', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '5 days', 4, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP);

-- Insertion de membres dans les projets avec rôles
INSERT INTO project_members (project_id, user_id, role, joined_at) VALUES
(1, 2, 'ADMIN', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(1, 3, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(1, 4, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(2, 1, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '8 days'),
(2, 3, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '8 days'),
(2, 4, 'OBSERVER', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 5, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(3, 2, 'ADMIN', CURRENT_TIMESTAMP - INTERVAL '13 days'),
(3, 4, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 1, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '35 days'),
(4, 2, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '35 days'),
(5, 1, 'ADMIN', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(5, 2, 'MEMBER', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Insertion de tâches de test avec dates de fin pour certaines
INSERT INTO tasks (title, description, status, priority, due_date, end_date, project_id, assigned_user_id, created_at, updated_at) VALUES
('Conception de la base de données', 'Créer le schéma de la base de données avec toutes les tables nécessaires', 'DONE', 'HIGH', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '12 days', 1, 1, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '12 days'),
('Développement du backend Spring Boot', 'Implémenter les contrôleurs et services pour les utilisateurs et projets', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', NULL, 1, 2, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP),
('Création des composants Angular', 'Développer les composants pour la gestion des utilisateurs', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '10 days', NULL, 1, 3, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP),
('Tests unitaires backend', 'Écrire les tests unitaires pour les services et contrôleurs', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '7 days', NULL, 1, 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
('Tests unitaires frontend', 'Écrire les tests unitaires pour les composants Angular', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '12 days', NULL, 1, 3, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
('Analyse des besoins', 'Analyser et documenter les besoins du projet', 'DONE', 'HIGH', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 2, 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('Architecture technique', 'Définir l''architecture technique de l''application', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', NULL, 2, 1, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
('Développement API REST', 'Créer les endpoints REST pour les ressources principales', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', NULL, 3, 1, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP),
('Documentation API', 'Documenter l''API avec Swagger/OpenAPI', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '8 days', NULL, 3, 4, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
('Design de la page d''accueil', 'Créer le design de la page d''accueil du site', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '30 days', 4, 3, CURRENT_TIMESTAMP - INTERVAL '38 days', CURRENT_TIMESTAMP - INTERVAL '30 days'),
('Intégration du contenu', 'Intégrer le contenu fourni par le client', 'DONE', 'MEDIUM', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 4, 4, CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('Développement du module de recrutement', 'Créer le module de gestion des candidatures', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '10 days', NULL, 5, 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
('Interface de gestion des employés', 'Développer l''interface pour gérer les informations des employés', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '15 days', NULL, 5, 2, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP);

-- Insertion d'historique des modifications de tâches
INSERT INTO task_history (task_id, user_id, field_name, old_value, new_value, change_type, created_at) VALUES
(1, 1, 'status', 'TODO', 'IN_PROGRESS', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '16 days'),
(1, 1, 'status', 'IN_PROGRESS', 'DONE', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(1, 1, 'end_date', NULL, CURRENT_TIMESTAMP - INTERVAL '12 days'::text, 'UPDATED', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(2, 2, 'status', 'TODO', 'IN_PROGRESS', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '13 days'),
(2, 2, 'assignedUser', NULL, 'jane.smith@example.com', 'ASSIGNED', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 3, 'priority', 'LOW', 'MEDIUM', 'UPDATED', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 2, 'status', 'IN_PROGRESS', 'DONE', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(6, 2, 'end_date', NULL, CURRENT_TIMESTAMP - INTERVAL '5 days'::text, 'UPDATED', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(7, 1, 'status', 'TODO', 'IN_PROGRESS', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '6 days'),
(8, 1, 'status', 'TODO', 'IN_PROGRESS', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(10, 3, 'status', 'IN_PROGRESS', 'DONE', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(11, 4, 'status', 'IN_PROGRESS', 'DONE', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(11, 4, 'end_date', NULL, CURRENT_TIMESTAMP - INTERVAL '5 days'::text, 'UPDATED', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(12, 1, 'status', 'TODO', 'IN_PROGRESS', 'STATUS_CHANGED', CURRENT_TIMESTAMP - INTERVAL '1 days');

