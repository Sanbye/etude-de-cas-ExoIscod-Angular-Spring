# Application de Gestion de Projets

Application web de gestion de projets développée avec Angular (frontend) et Spring Boot (backend).

## Architecture

- **Backend**: Spring Boot 3.2.0 avec Java 17
- **Frontend**: Angular 17
- **Base de données**: PostgreSQL

## Structure du projet

```
.
├── backend/                 # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/iscod/projectmanagement/
│   │   │   │       ├── model/          # Entités JPA
│   │   │   │       ├── repository/     # Repositories Spring Data
│   │   │   │       └── config/         # Configurations
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/           # Tests unitaires et d'intégration
│   └── pom.xml
├── frontend/               # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/     # Modèles TypeScript
│   │   │   ├── users/      # Module utilisateurs
│   │   │   ├── projects/   # Module projets
│   │   │   └── tasks/      # Module tâches
│   │   └── styles.css
│   └── package.json
└── database/              # Scripts SQL
    ├── schema.sql         # Schéma de la base de données
    └── data.sql           # Données de test
```

## Entités principales

### User (Utilisateur)
- Informations personnelles (nom, prénom, email, username)
- Relation avec les projets (propriétaire ou membre)
- Tâches assignées

### Project (Projet)
- Nom, description, statut
- Propriétaire (User)
- Membres (liste de Users)
- Liste des tâches

### Task (Tâche)
- Titre, description
- Statut (TODO, IN_PROGRESS, DONE, CANCELLED)
- Priorité (LOW, MEDIUM, HIGH, URGENT)
- Projet associé
- Utilisateur assigné

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- Node.js 18+ et npm
- PostgreSQL 12+

## Installation et démarrage

### Base de données

1. Créer la base de données PostgreSQL :
```sql
CREATE DATABASE project_management;
```

2. Exécuter les scripts SQL dans l'ordre :
```bash
psql -U postgres -d project_management -f database/schema.sql
psql -U postgres -d project_management -f database/data.sql
```

### Backend

1. Naviguer vers le dossier backend :
```bash
cd backend
```

2. Installer les dépendances et compiler :
```bash
# Sur Windows (PowerShell)
.\mvnw.cmd clean install

# Sur Linux/Mac
./mvnw clean install
```

3. Lancer l'application :
```bash
# Sur Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Sur Linux/Mac
./mvnw spring-boot:run
```

**Note**: Le projet utilise le Maven Wrapper (`mvnw`), donc vous n'avez pas besoin d'installer Maven globalement. Si vous avez Maven installé, vous pouvez aussi utiliser `mvn` directement.

Le backend sera accessible sur `http://localhost:8080`

### Frontend

1. Naviguer vers le dossier frontend :
```bash
cd frontend
```

2. Installer les dépendances :
```bash
npm install
```

3. Lancer l'application :
```bash
npm start
```

Le frontend sera accessible sur `http://localhost:4200`

## API Endpoints

### Users
- `GET /api/users` - Liste tous les utilisateurs
- `GET /api/users/{id}` - Détails d'un utilisateur
- `POST /api/users` - Créer un utilisateur
- `PUT /api/users/{id}` - Mettre à jour un utilisateur
- `DELETE /api/users/{id}` - Supprimer un utilisateur

### Projects
- `GET /api/projects` - Liste tous les projets
- `GET /api/projects/{id}` - Détails d'un projet
- `POST /api/projects` - Créer un projet
- `PUT /api/projects/{id}` - Mettre à jour un projet
- `DELETE /api/projects/{id}` - Supprimer un projet

### Tasks
- `GET /api/tasks` - Liste toutes les tâches
- `GET /api/tasks/{id}` - Détails d'une tâche
- `POST /api/tasks` - Créer une tâche
- `PUT /api/tasks/{id}` - Mettre à jour une tâche
- `DELETE /api/tasks/{id}` - Supprimer une tâche

## Tests

### Backend
```bash
cd backend
# Sur Windows
.\mvnw.cmd test

# Sur Linux/Mac
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## Développement

Ce projet est en cours de développement. Les fonctionnalités suivantes seront ajoutées :
- Services et contrôleurs REST complets
- Tests unitaires et d'intégration
- Interface utilisateur complète avec formulaires CRUD
- Dockerisation
- CI/CD avec GitHub Actions

