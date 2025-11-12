-- Script d'insertion des données de test

-- Insertion d'utilisateurs de test
INSERT INTO users (username, email, first_name, last_name, password, created_at, updated_at) VALUES
('john.doe', 'john.doe@example.com', 'John', 'Doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', 'Jane', 'Smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob.johnson', 'bob.johnson@example.com', 'Bob', 'Johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('alice.brown', 'alice.brown@example.com', 'Alice', 'Brown', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertion de projets de test
INSERT INTO projects (name, description, status, owner_id, created_at, updated_at) VALUES
('Application Web E-commerce', 'Développement d''une application web e-commerce avec Angular et Spring Boot', 'IN_PROGRESS', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Système de Gestion de Projets', 'Application de gestion de projets avec suivi des tâches', 'PLANNED', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('API REST pour Mobile', 'Développement d''une API REST pour application mobile', 'IN_PROGRESS', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Site Vitrine Entreprise', 'Création d''un site vitrine pour présenter l''entreprise', 'COMPLETED', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertion de membres dans les projets
INSERT INTO project_members (project_id, user_id) VALUES
(1, 2),
(1, 3),
(2, 1),
(2, 3),
(2, 4),
(3, 2),
(3, 4);

-- Insertion de tâches de test
INSERT INTO tasks (title, description, status, priority, due_date, project_id, assigned_user_id, created_at, updated_at) VALUES
('Conception de la base de données', 'Créer le schéma de la base de données avec toutes les tables nécessaires', 'DONE', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '5 days', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Développement du backend Spring Boot', 'Implémenter les contrôleurs et services pour les utilisateurs et projets', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '10 days', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Création des composants Angular', 'Développer les composants pour la gestion des utilisateurs', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '15 days', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tests unitaires backend', 'Écrire les tests unitaires pour les services et contrôleurs', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '12 days', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tests unitaires frontend', 'Écrire les tests unitaires pour les composants Angular', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '18 days', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Analyse des besoins', 'Analyser et documenter les besoins du projet', 'DONE', 'HIGH', CURRENT_TIMESTAMP - INTERVAL '5 days', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Architecture technique', 'Définir l''architecture technique de l''application', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '3 days', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Développement API REST', 'Créer les endpoints REST pour les ressources principales', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '7 days', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Documentation API', 'Documenter l''API avec Swagger/OpenAPI', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '9 days', 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Design de la page d''accueil', 'Créer le design de la page d''accueil du site', 'DONE', 'LOW', CURRENT_TIMESTAMP - INTERVAL '10 days', 4, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Intégration du contenu', 'Intégrer le contenu fourni par le client', 'DONE', 'MEDIUM', CURRENT_TIMESTAMP - INTERVAL '5 days', 4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

