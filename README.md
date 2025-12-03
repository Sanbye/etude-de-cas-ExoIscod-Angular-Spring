# Application de Gestion de Projets

[![CI](https://github.com/Sanbye/etude-de-cas-ExoIscod-Angular-Spring/actions/workflows/ci.yml/badge.svg)](https://github.com/Sanbye/etude-de-cas-ExoIscod-Angular-Spring/actions/workflows/ci.yml)

Application web de gestion de projets développée avec Angular (frontend) et Spring Boot (backend).

## Architecture

- **Backend**: Spring Boot 3.2.0 avec Java 17
- **Frontend**: Angular 17
- **Base de données**: PostgreSQL

## Structure du projet

```
.
├── .github/
│   └── workflows/
│       └── ci.yml         # Configuration CI/CD GitHub Actions
├── backend/                 # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/codeSolution/PMT/
│   │   │   │       ├── model/          # Entités JPA
│   │   │   │       ├── repository/     # Repositories Spring Data
│   │   │   │       ├── service/        # Services métier
│   │   │   │       ├── controller/     # Contrôleurs REST
│   │   │   │       └── config/         # Configurations
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/           # Tests unitaires et d'intégration
│   │       ├── java/
│   │       │   └── com/codeSolution/PMT/
│   │       │       ├── service/        # Tests unitaires des services
│   │       │       └── repository/     # Tests d'intégration des repositories
│   │       └── resources/
│   │           └── application-test.properties
│   └── pom.xml
├── frontend/               # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/     # Modèles TypeScript
│   │   │   ├── services/   # Services Angular
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

## CI/CD

Le projet utilise GitHub Actions pour l'intégration continue. Le workflow CI s'exécute automatiquement sur chaque push et pull request vers la branche `main`.

### Workflow CI

Le workflow CI (`.github/workflows/ci.yml`) effectue les actions suivantes :

1. **Tests Backend** : Exécute les tests unitaires et d'intégration avec Maven
   - Utilise Java 17
   - Exécute `mvnw test` dans le dossier `backend`

2. **Tests Frontend** : Exécute les tests unitaires Angular
   - Utilise Node.js 18
   - Exécute `npm test` avec ChromeHeadless dans le dossier `frontend`

### Badge de statut

Le badge de statut CI est affiché en haut du README. Pour l'activer, remplacez `votre-username` dans l'URL du badge par votre nom d'utilisateur GitHub.

## Tests

Le projet inclut une suite complète de tests unitaires et d'intégration pour le backend et le frontend.

### Tests Backend

Les tests backend utilisent :
- **JUnit 5** pour les tests unitaires
- **Mockito** pour le mocking des dépendances
- **H2 Database** en mémoire pour les tests d'intégration des repositories
- **Spring Boot Test** pour l'intégration avec Spring

#### Structure des tests backend

- **Tests unitaires des services** : `backend/src/test/java/com/codeSolution/PMT/service/`
  - `UserServiceTest.java` - Tests du service utilisateur
  - `ProjectServiceTest.java` - Tests du service projet
  - `TaskServiceTest.java` - Tests du service tâche
  - `AuthServiceTest.java` - Tests du service d'authentification

- **Tests d'intégration des repositories** : `backend/src/test/java/com/codeSolution/PMT/repository/`
  - `UserRepositoryTest.java` - Tests du repository utilisateur

#### Exécuter les tests backend

```bash
cd backend

# Sur Windows (PowerShell)
.\mvnw.cmd test

# Sur Linux/Mac
./mvnw test
```

#### Configuration des tests

Les tests utilisent une configuration séparée définie dans `backend/src/test/resources/application-test.properties` :
- Base de données H2 en mémoire
- Configuration JPA adaptée pour les tests
- Désactivation des fonctionnalités non nécessaires (emails, etc.)

### Tests Frontend

Les tests frontend utilisent :
- **Jasmine** comme framework de test
- **Karma** comme test runner
- **Angular Testing Utilities** (TestBed, HttpClientTestingModule)
- **ChromeHeadless** pour l'exécution en CI

#### Structure des tests frontend

- **Tests des services** : `frontend/src/app/services/`
  - `user.service.spec.ts` - Tests du service utilisateur
  - `project.service.spec.ts` - Tests du service projet
  - `task.service.spec.ts` - Tests du service tâche

- **Tests des composants** : `frontend/src/app/`
  - `app.component.spec.ts` - Tests du composant principal
  - `users/user-list/user-list.component.spec.ts` - Tests du composant liste utilisateurs
  - `projects/project-list/project-list.component.spec.ts` - Tests du composant liste projets
  - `tasks/task-list/task-list.component.spec.ts` - Tests du composant liste tâches

#### Exécuter les tests frontend

```bash
cd frontend

# Exécuter les tests en mode watch (développement)
npm test

# Exécuter les tests une fois (CI)
npm test -- --watch=false --browsers=ChromeHeadless

# Exécuter les tests avec couverture de code
npm run test:coverage
```

### Couverture de code

#### Backend

Pour générer un rapport de couverture avec Maven, vous pouvez utiliser JaCoCo :

```bash
cd backend
.\mvnw.cmd clean test jacoco:report
```

Le rapport sera généré dans `backend/target/site/jacoco/index.html`

#### Frontend

La couverture de code est automatiquement générée lors de l'exécution des tests :

```bash
cd frontend
npm run test:coverage
```

Le rapport sera généré dans `frontend/coverage/index.html`

## Développement

### Contribution

Pour contribuer au projet :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/ma-fonctionnalite`)
3. Commiter vos changements (`git commit -m 'Ajout de ma fonctionnalité'`)
4. Pousser vers la branche (`git push origin feature/ma-fonctionnalite`)
5. Ouvrir une Pull Request

### Standards de code

- **Backend** : Suivre les conventions Java et Spring Boot
- **Frontend** : Suivre les conventions Angular et TypeScript
- **Tests** : Maintenir une couverture de code élevée (>80%)
- **Commits** : Utiliser des messages de commit clairs et descriptifs

### Fonctionnalités à venir

- Interface utilisateur complète avec formulaires CRUD
- Authentification JWT complète
- Dockerisation de l'application
- Déploiement automatisé avec GitHub Actions

