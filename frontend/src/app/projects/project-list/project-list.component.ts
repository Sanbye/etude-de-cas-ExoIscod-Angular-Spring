import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { SessionService } from '../../services/session.service';

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

      <div *ngIf="!loading && !error && projects.length > 0" class="sections">
        <section class="section section--mine" *ngIf="myProjects.length > 0">
          <div class="section-header">
            <h3 class="section-title">Mes projets</h3>
            <span class="section-count">{{ myProjects.length }}</span>
          </div>
          <div class="projects-grid">
            <div *ngFor="let project of myProjects" class="project-card project-card--mine" (click)="viewProject(project.id!)">
              <div class="card-top">
                <h4>{{ project.name }}</h4>
                <span class="badge" [ngClass]="getRoleBadgeClass(project.id)" *ngIf="project.id">
                  {{ getRoleLabel(project.id) }}
                </span>
              </div>
              <p class="description">{{ project.description || 'Aucune description' }}</p>
              <div class="project-meta" *ngIf="project.startingDate">
                <span class="date">
                  Date de début: {{ project.startingDate | date:'dd/MM/yyyy' }}
                </span>
              </div>
            </div>
          </div>
        </section>

        <section class="section section--others" *ngIf="otherProjects.length > 0">
          <div class="section-header">
            <h3 class="section-title">Autres projets</h3>
            <span class="section-count">{{ otherProjects.length }}</span>
          </div>
          <div class="projects-grid">
            <div *ngFor="let project of otherProjects" class="project-card" (click)="viewProject(project.id!)">
              <h4>{{ project.name }}</h4>
              <p class="description">{{ project.description || 'Aucune description' }}</p>
              <div class="project-meta" *ngIf="project.startingDate">
                <span class="date">
                  Date de début: {{ project.startingDate | date:'dd/MM/yyyy' }}
                </span>
              </div>
            </div>
          </div>
        </section>
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
      cursor: pointer;
      border: 1px solid rgba(44, 62, 80, 0.08);
    }
    .project-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    }
    .project-card h4 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
      font-size: 1.05rem;
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

    .sections {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }
    .section {
      border-radius: 10px;
      padding: 1rem;
      background: #ffffff;
      border: 1px solid rgba(44, 62, 80, 0.08);
    }
    .section--mine {
      background: rgba(39, 174, 96, 0.06);
      border-color: rgba(39, 174, 96, 0.22);
    }
    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 1rem;
    }
    .section-title {
      margin: 0;
      color: #2c3e50;
    }
    .section-count {
      font-size: 0.85rem;
      color: #7f8c8d;
      background: rgba(127, 140, 141, 0.12);
      padding: 0.25rem 0.6rem;
      border-radius: 999px;
    }
    .project-card--mine {
      border: 1px solid rgba(39, 174, 96, 0.28);
      background: #ffffff;
    }
    .card-top {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 0.75rem;
    }
    .badge {
      font-size: 0.75rem;
      font-weight: 700;
      padding: 0.2rem 0.5rem;
      border-radius: 999px;
      white-space: nowrap;
    }
    .badge--admin {
      color: #7d3c98;
      background: rgba(155, 89, 182, 0.14);
      border: 1px solid rgba(155, 89, 182, 0.28);
    }
    .badge--member {
      color: #1e8449;
      background: rgba(39, 174, 96, 0.14);
      border: 1px solid rgba(39, 174, 96, 0.25);
    }
    .badge--viewer {
      color: #2471a3;
      background: rgba(52, 152, 219, 0.14);
      border: 1px solid rgba(52, 152, 219, 0.25);
    }
    .badge--unknown {
      color: #566573;
      background: rgba(127, 140, 141, 0.12);
      border: 1px solid rgba(127, 140, 141, 0.25);
    }
  `]
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  myProjects: Project[] = [];
  otherProjects: Project[] = [];
  myProjectRoles: Record<string, string> = {};
  loading = true;
  error: string | null = null;

  constructor(
    private projectService: ProjectService,
    private sessionService: SessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.error = null;

    const currentUser = this.sessionService.getCurrentUser();
    const userId = currentUser?.userId;

    const all$ = this.projectService.getAllProjects();
    const mine$ = userId
      ? this.projectService.getProjectsByMember(userId).pipe(
          catchError((e) => {
            // Si l'endpoint n'est pas dispo ou renvoie une erreur, on garde juste une liste vide
            console.warn('Impossible de charger "mes projets" via /projects/member/{id}', e);
            return of([] as Project[]);
          })
        )
      : of([] as Project[]);

    forkJoin({ all: all$, mine: mine$ }).subscribe({
      next: ({ all, mine }) => {
        this.projects = all ?? [];

        const myIds = new Set((mine ?? []).map(p => p.id).filter((id): id is string => !!id));
        this.myProjects = this.projects.filter(p => !!p.id && myIds.has(p.id));
        this.otherProjects = this.projects.filter(p => !p.id || !myIds.has(p.id));

        if (userId && this.myProjects.length > 0) {
          this.loadMyProjectRoles(userId);
        } else {
          this.myProjectRoles = {};
        }

        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des projets:', err);
        this.error = 'Impossible de charger les projets. Vérifiez que le backend est démarré.';
        this.loading = false;
      }
    });
  }

  private loadMyProjectRoles(userId: string): void {
    const roleRequests = this.myProjects
      .filter(p => !!p.id)
      .map(p =>
        this.projectService.getProjectMembers(p.id!).pipe(
          catchError((e) => {
            console.warn(`Impossible de charger les membres du projet ${p.id}`, e);
            return of([] as any[]);
          })
        )
      );

    if (roleRequests.length === 0) {
      this.myProjectRoles = {};
      return;
    }

    forkJoin(roleRequests).subscribe({
      next: (membersByProject) => {
        const roles: Record<string, string> = {};
        this.myProjects.forEach((p, idx) => {
          if (!p.id) return;
          const members = membersByProject[idx] ?? [];
          const me = members.find((m: any) => (m?.userId ?? m?.user_id) === userId);
          const role = (me?.role ?? '').toString().toUpperCase();
          if (role) roles[p.id] = role;
        });
        this.myProjectRoles = roles;
      },
      error: (e) => {
        console.warn('Impossible de charger les rôles des projets', e);
        this.myProjectRoles = {};
      }
    });
  }

  getRoleLabel(projectId: string): string {
    const role = (this.myProjectRoles[projectId] ?? '').toUpperCase();
    if (!role) return 'Rôle inconnu';
    // Roles côté backend: ADMIN, MEMBER (potentiellement d'autres)
    return role;
  }

  getRoleBadgeClass(projectId: string): string {
    const role = (this.myProjectRoles[projectId] ?? '').toUpperCase();
    switch (role) {
      case 'ADMIN':
        return 'badge--admin';
      case 'MEMBER':
        return 'badge--member';
      case 'VIEWER':
        return 'badge--viewer';
      default:
        return 'badge--unknown';
    }
  }

  viewProject(projectId: string): void {
    this.router.navigate(['/projects', projectId]);
  }
}

