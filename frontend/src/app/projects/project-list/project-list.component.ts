import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="project-list-container">
      <div class="header-actions">
        <h2>Liste des Projets</h2>
        <a routerLink="/projects/create" class="btn-create">+ Nouveau projet</a>
      </div>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="!loading && !error && projects.length === 0" class="empty">Aucun projet trouvé.</div>
      <div *ngIf="!loading && !error && projects.length > 0" class="projects-grid">
        <div *ngFor="let project of projects" class="project-card">
          <h3>{{ project.name }}</h3>
          <p class="description">{{ project.description || 'Aucune description' }}</p>
          <div class="project-meta" *ngIf="project.startingDate">
            <span class="date">
              Date de début: {{ project.startingDate | date:'dd/MM/yyyy' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .project-list-container {
      max-width: 1200px;
      margin: 0 auto;
    }
    .header-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    h2 {
      color: #2c3e50;
      margin: 0;
    }
    .btn-create {
      background-color: #27ae60;
      color: white;
      text-decoration: none;
      padding: 0.75rem 1.5rem;
      border-radius: 4px;
      font-weight: 600;
      transition: background-color 0.3s;
    }
    .btn-create:hover {
      background-color: #229954;
    }
    .projects-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    .project-card {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: transform 0.2s, box-shadow 0.2s;
    }
    .project-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    }
    .project-card h3 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
    }
    .description {
      color: #7f8c8d;
      margin: 0.5rem 0;
      font-size: 0.9rem;
    }
    .project-meta {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      margin-top: 1rem;
    }
    .date {
      font-size: 0.85rem;
      color: #7f8c8d;
    }
    .loading {
      text-align: center;
      padding: 2rem;
      color: #7f8c8d;
    }
    .error {
      text-align: center;
      padding: 2rem;
      color: #e74c3c;
      background-color: #f8d7da;
      border-radius: 8px;
      margin: 1rem 0;
    }
    .empty {
      text-align: center;
      padding: 2rem;
      color: #7f8c8d;
    }
  `]
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  loading = true;
  error: string | null = null;

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.error = null;
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des projets:', err);
        this.error = 'Impossible de charger les projets. Vérifiez que le backend est démarré.';
        this.loading = false;
      }
    });
  }
}

