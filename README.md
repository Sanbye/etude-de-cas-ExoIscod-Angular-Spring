# Application de Gestion de Projets

[![CI](https://github.com/Sanbye/etude-de-cas-ExoIscod-Angular-Spring/actions/workflows/ci.yml/badge.svg)](https://github.com/Sanbye/etude-de-cas-ExoIscod-Angular-Spring/actions/workflows/ci.yml)

Application web de gestion de projets développée avec Angular (frontend) et Spring Boot (backend).

## Architecture

- **Backend**: Spring Boot 3.2.0 avec Java 17
- **Frontend**: Angular 17
- **Base de données**: PostgreSQL
- **Containerisation**: Docker avec images disponibles sur DockerHub

## Structure du projet

```
.
├── .github/
│   └── workflows/
│       └── ci.yml                    # Configuration CI/CD GitHub Actions
├── backend/                          # Application Spring Boot
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties  # Configuration Maven Wrapper
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/codeSolution/PMT/
│   │   │   │       ├── model/          # Entités JPA
│   │   │   │       ├── repository/    # Repositories Spring Data
│   │   │   │       ├── service/        # Services métier
│   │   │   │       ├── controller/     # Contrôleurs REST
│   │   │   │       ├── config/         # Configurations
│   │   │   │       └── dto/            # Data Transfer Objects
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                      # Tests unitaires et d'intégration
│   │       ├── java/
│   │       │   └── com/codeSolution/PMT/
│   │       │       ├── service/        # Tests unitaires des services
│   │       │       ├── repository/     # Tests d'intégration des repositories
│   │       │       └── controller/     # Tests d'intégration des contrôleurs
│   │       └── resources/
│   │           └── application-test.properties
│   ├── Dockerfile                     # Image Docker pour le backend
│   ├── .dockerignore                  # Fichiers exclus du build Docker
│   ├── mvnw                           # Maven Wrapper (Linux/Mac)
│   ├── mvnw.cmd                        # Maven Wrapper (Windows)
│   └── pom.xml
├── frontend/                          # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/                # Modèles TypeScript
│   │   │   ├── services/               # Services Angular
│   │   │   ├── users/                  # Module utilisateurs
│   │   │   ├── projects/               # Module projets
│   │   │   └── tasks/                  # Module tâches
│   │   └── styles.css
│   ├── Dockerfile                      # Image Docker pour le frontend
│   ├── .dockerignore                  # Fichiers exclus du build Docker
│   └── package.json
└── database/                          # Scripts SQL
    ├── create_database_utf8.sql        # Script pour créer la base avec encodage UTF-8
    ├── schema.sql                      # Schéma de la base de données
    └── data.sql                        # Données de test
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

### Pour le développement local

- **Java 17** ou supérieur
- **Maven 3.6+** (ou utilisation du Maven Wrapper inclus)
- **Node.js 18+** et npm
- **PostgreSQL 18+**

### Pour la conteneurisation

- **Docker**

## Installation et démarrage

Clonez le dépôt du projet avec la commande suivante :
```bash
git clone https://github.com/Sanbye/etude-de-cas-ExoIscod-Angular-Spring.git
```

### Option 1 : Développement local

#### Base de données

1. **Créer la base de données PostgreSQL avec UTF-8** :

> **Important :** Sous Windows, assurez-vous que le chemin d'accès à PostgreSQL est correctement configuré dans les variables d'environnement.

**Option A : Utiliser le script (recommandé)** :
```bash
psql -U postgres -f database/create_database_utf8.sql
```

**Option B : Créer manuellement** :
```sql
DROP DATABASE IF EXISTS project_management;
CREATE DATABASE project_management
    WITH 
    ENCODING 'UTF8'
    LC_COLLATE='fr_FR.UTF-8'
    LC_CTYPE='fr_FR.UTF-8'
    TEMPLATE template0;
```

2. **Exécuter les scripts SQL dans l'ordre** :

User: postgres
Mot de passe: postgres

```bash
psql -U postgres -d project_management -f database/schema.sql
psql -U postgres -d project_management -f database/data.sql
```

#### Backend

1. Naviguer vers le dossier backend :
```bash
cd backend
```

2. Le projet utilise le **Maven Wrapper** (`mvnw`/`mvnw.cmd`), donc vous n'avez pas besoin d'installer Maven globalement. Le wrapper détecte automatiquement Java 17.

3. Installer les dépendances et compiler :
```bash
# Sur Windows (PowerShell)
.\mvnw.cmd clean install

# Sur Linux/Mac
./mvnw clean install
```

4. Lancer l'application :
```bash
# Sur Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Sur Linux/Mac
./mvnw spring-boot:run
```

**Note**: Le Maven Wrapper détecte automatiquement Java 17. Si vous avez plusieurs versions de Java installées, assurez-vous que Java 17 est disponible dans votre PATH ou définissez `JAVA_HOME` manuellement.

Le backend sera accessible sur `http://localhost:8080`

#### Frontend

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

Le frontend sera accessible sur `http://localhost:4200` (**port 4200 obligatoire**).

### Option 2 : Docker

#### Prérequis

- Docker installé et en cours d'exécution
- Accès à DockerHub pour pull les images (ou build local)

#### Méthode 1 : Utiliser les images DockerHub

Les images Docker sont automatiquement construites et poussées sur DockerHub lors des push sur la branche `main`.

1. **Pull les images Docker** :
```bash
docker pull gossandev/pmt-backend:latest
docker pull gossandev/pmt-frontend:latest
```

2. **Créer un réseau Docker** (pour la communication entre conteneurs) :
```bash
docker network create pmt-network
```

3. **Lancer un conteneur PostgreSQL** :

**Sur Linux/Mac (bash)** :
```bash
docker run -d \
  --name pmt-postgres \
  --network pmt-network \
  -e POSTGRES_DB=project_management \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

**Sur Windows (PowerShell)** :
```powershell
docker run -d --name pmt-postgres --network pmt-network -e POSTGRES_DB=project_management -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:15-alpine
```

4. **Initialiser la base de données** :

**Sur Linux/Mac (bash)** :
```bash
docker exec -i pmt-postgres psql -U postgres -d project_management < database/schema.sql
```
```bash
docker exec -i pmt-postgres psql -U postgres -d project_management < database/data.sql
```

**Sur Windows (PowerShell)** :
```powershell
Get-Content -Raw -Encoding UTF8 database/schema.sql | docker exec -i pmt-postgres psql -U postgres -d project_management
```
```powershell
Get-Content -Raw -Encoding UTF8 database/data.sql | docker exec -i pmt-postgres psql -U postgres -d project_management
```

5. **Lancer le backend** :

**Sur Linux/Mac (bash)** :
```bash
docker run -d \
  --name pmt-backend \
  --network pmt-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pmt-postgres:5432/project_management \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8" \
  -e SERVER_PORT=3000 \
  -p 3000:3000 \
  gossandev/pmt-backend:latest
```

**Sur Windows (PowerShell)** :
```powershell
docker run -d --name pmt-backend --network pmt-network -e SPRING_DATASOURCE_URL=jdbc:postgresql://pmt-postgres:5432/project_management -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres -e JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8" -e SERVER_PORT=3000 -p 3000:3000 gossandev/pmt-backend:latest
```

Le backend Docker sera accessible sur `http://localhost:3000`

6. **Lancer le frontend** :

**Sur Linux/Mac (bash)** :
```bash
docker run -d \
  --name pmt-frontend \
  --network pmt-network \
  -p 4200:8080 \
  gossandev/pmt-frontend:latest
```

**Sur Windows (PowerShell)** :
```powershell
docker run -d --name pmt-frontend --network pmt-network -p 4200:8080 gossandev/pmt-frontend:latest
```

Le frontend Docker sera accessible sur `http://localhost:4200` (**port 4200 obligatoire**).

#### Méthode 2 : Build local des images Docker

Si vous préférez construire les images localement :

1. **Build l'image backend** :

```bash
cd backend
docker build -t pmt-backend:latest .
cd ..
```

2. **Build l'image frontend** :

```bash
cd frontend
docker build -t pmt-frontend:latest .
cd ..
```

3. **Suivre les étapes 2-6 de la Méthode 1** en remplaçant `gossandev/pmt-backend:latest` par `pmt-backend:latest` et `gossandev/pmt-frontend:latest` par `pmt-frontend:latest`.

#### Arrêter les conteneurs

```bash
docker stop pmt-backend pmt-frontend pmt-postgres
docker rm pmt-backend pmt-frontend pmt-postgres
```

## Comptes de test

Pour faciliter les tests de l'application, des comptes utilisateurs sont pré-configurés dans la base de données. **Tous les utilisateurs ont le même mot de passe : `123456`**

### Comptes principaux pour tester les User Stories

Ces comptes sont liés au projet "Projet de Test US" et permettent de tester toutes les fonctionnalités :

#### Compte Administrateur
- **Email** : `admin@admin.gmail.com`
- **Nom d'utilisateur** : `admin`
- **Mot de passe** : `123456`
- **Rôle dans le projet "Projet de Test US"** : `ADMIN`
- **Fonctionnalités testables** :
  - ✅ Créer un nouveau projet
  - ✅ Inviter des membres au projet
  - ✅ Attribuer des rôles aux membres (ADMIN, MEMBER, OBSERVER)
  - ✅ Créer des tâches
  - ✅ Assigner des tâches à des membres

#### Compte Membre
- **Email** : `member@member.gmail.com`
- **Nom d'utilisateur** : `member`
- **Mot de passe** : `123456`
- **Rôle dans le projet "Projet de Test US"** : `MEMBER`
- **Fonctionnalités testables** :
  - ✅ Créer des tâches
  - ✅ Assigner des tâches à des membres
  - ❌ Inviter des membres (réservé aux ADMIN)
  - ❌ Modifier les rôles (réservé aux ADMIN)

### Projet de test

Le projet **"Projet de Test US"** contient :
- **2 membres** : admin (ADMIN) et member (MEMBER)
- **4 tâches** :
  - "Tâche assignée à Admin" (TODO, HIGH) - assignée à admin
  - "Tâche en cours - Admin" (IN_PROGRESS, MEDIUM) - assignée à admin
  - "Tâche assignée à Member" (TODO, MEDIUM) - assignée à member
  - "Tâche terminée" (DONE, LOW) - assignée à admin

### Scénarios de test recommandés

1. **Test avec le compte ADMIN** (`admin@admin.gmail.com` / `123456`) :
   - Se connecter
   - Voir le projet "Projet de Test US"
   - Cliquer sur le projet pour voir les détails
   - Inviter un nouveau membre (utiliser un email existant comme `john.doe@example.com`)
   - Modifier le rôle d'un membre existant
   - Créer une nouvelle tâche
   - Aller dans l'onglet "Tâches" et assigner une tâche à un membre

2. **Test avec le compte MEMBER** (`member@member.gmail.com` / `123456`) :
   - Se connecter
   - Voir le projet "Projet de Test US"
   - Cliquer sur le projet pour voir les détails
   - Vérifier que la section "Inviter un membre" n'est pas visible
   - Créer une nouvelle tâche
   - Aller dans l'onglet "Tâches" et assigner une tâche à un membre


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

### Authentication
- `POST /api/auth/register` - S'inscrire
- `POST /api/auth/login` - Se connecter

## CI/CD

Le projet utilise GitHub Actions pour l'intégration continue et le déploiement continu. Le workflow CI/CD s'exécute automatiquement sur chaque push et pull request vers la branche `main`.

### Workflow CI/CD

Le workflow CI/CD (`.github/workflows/ci.yml`) effectue les actions suivantes :

1. **Tests Backend** : Exécute les tests unitaires et d'intégration avec Maven
   - Utilise Java 17
   - Exécute `./mvnw test` dans le dossier `backend`
   - Génère un rapport de couverture de code

2. **Tests Frontend** : Exécute les tests unitaires Angular
   - Utilise Node.js 18
   - Exécute `npm test` avec ChromeHeadless dans le dossier `frontend`
   - Génère un rapport de couverture de code

3. **Build Docker Images** (uniquement sur push vers `main`) :
   - Construit les images Docker pour le backend et le frontend
   - Utilise le cache Docker pour optimiser les builds
   - Vérifie que les images se construisent correctement

4. **Push Docker Images** (uniquement sur push vers `main`) :
   - Push les images Docker vers DockerHub
   - Tags : `gossandev/pmt-backend:latest` et `gossandev/pmt-frontend:latest`

### Badge de statut

Le badge de statut CI est affiché en haut du README. Pour l'activer, remplacez `Sanbye` dans l'URL du badge par votre nom d'utilisateur GitHub.

## Tests

Le projet inclut une suite complète de tests unitaires et d'intégration pour le backend et le frontend.

### Tests Backend

Les tests backend utilisent :
- **JUnit 5** pour les tests unitaires
- **Mockito** pour le mocking des dépendances
- **H2 Database** en mémoire pour les tests d'intégration des repositories
- **Spring Boot Test** pour l'intégration avec Spring
- **JaCoCo** pour la couverture de code

#### Structure des tests backend

- **Tests unitaires des services** : `backend/src/test/java/com/codeSolution/PMT/service/`
  - `UserServiceTest.java` - Tests du service utilisateur
  - `ProjectServiceTest.java` - Tests du service projet
  - `TaskServiceTest.java` - Tests du service tâche
  - `AuthServiceTest.java` - Tests du service d'authentification

- **Tests d'intégration des repositories** : `backend/src/test/java/com/codeSolution/PMT/repository/`
  - `UserRepositoryTest.java` - Tests du repository utilisateur

- **Tests d'intégration des contrôleurs** : `backend/src/test/java/com/codeSolution/PMT/controller/`
  - Tests des endpoints REST

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

# Sur Windows (PowerShell)
.\mvnw.cmd clean test jacoco:report

# Sur Linux/Mac
./mvnw clean test jacoco:report
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

### Maven Wrapper

Le projet utilise le **Maven Wrapper** 

Les fichiers `mvnw` (Linux/Mac) et `mvnw.cmd` (Windows) sont inclus dans le projet et doivent être commités dans Git.

### Docker

Les images Docker sont construites avec :
- **Backend** : Multi-stage build avec Maven pour la compilation et JRE Alpine pour l'exécution
- **Frontend** : Multi-stage build avec Node.js pour la compilation et Nginx Alpine pour servir les fichiers statiques