import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="project-list-container">
      <h2>Liste des Projets</h2>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="!loading && !error && projects.length === 0" class="empty">Aucun projet trouvé.</div>
      <div *ngIf="!loading && !error && projects.length > 0" class="projects-grid">
        <div *ngFor="let project of projects" class="project-card">
          <h3>{{ project.name }}</h3>
          <p class="description">{{ project.description || 'Aucune description' }}</p>
          <div class="project-meta">
            <span class="status" [class]="'status-' + project.status.toLowerCase()">
              {{ project.status }}
            </span>
            <span class="owner" *ngIf="project.owner">
              Propriétaire: {{ project.owner.firstName }} {{ project.owner.lastName }}
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
    h2 {
      color: #2c3e50;
      margin-bottom: 1.5rem;
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
    .status {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.85rem;
      font-weight: 600;
      width: fit-content;
    }
    .status-planned { background-color: #e8f4f8; color: #3498db; }
    .status-in_progress { background-color: #fff3cd; color: #f39c12; }
    .status-completed { background-color: #d4edda; color: #27ae60; }
    .status-cancelled { background-color: #f8d7da; color: #e74c3c; }
    .owner {
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
  private apiUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.error = null;
    this.http.get<Project[]>(this.apiUrl).subscribe({
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

