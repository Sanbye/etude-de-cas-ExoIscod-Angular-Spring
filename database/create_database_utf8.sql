-- Script pour créer la base de données avec l'encodage UTF-8
-- Exécutez ce script en tant que superutilisateur PostgreSQL (postgres)
-- Avant d'exécuter schema.sql et data.sql

-- Fermer toutes les connexions actives à la base de données
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN SELECT pid FROM pg_stat_activity WHERE datname = 'project_management' AND pid <> pg_backend_pid()
    LOOP
        PERFORM pg_terminate_backend(r.pid);
    END LOOP;
END $$;

-- Supprimer la base de données si elle existe déjà
DROP DATABASE IF EXISTS project_management;

-- Créer la base de données avec l'encodage UTF-8
CREATE DATABASE project_management
    WITH 
    ENCODING 'UTF8'
    LC_COLLATE='fr_FR.UTF-8'
    LC_CTYPE='fr_FR.UTF-8'
    TEMPLATE template0;
