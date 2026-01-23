import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { SessionService } from '../../services/session.service';
import { Task } from '../../models/task.model';
import { Project } from '../../models/project.model';
import { AuthResponse } from '../../models/auth.model';

interface TaskGroup {
  project: Project;
  tasks: Task[];
  members: any[];
}

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="task-list-container">
      <h2>Liste des Tâches</h2>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="!loading && !error && taskGroups.length === 0" class="empty">Aucune tâche trouvée.</div>
      
      <div *ngFor="let group of taskGroups" class="project-group">
        <h3 class="project-title">{{ group.project.name }}</h3>
        <div class="tasks-grid">
          <div *ngFor="let task of group.tasks" class="task-card">
            <h4>{{ task.name }}</h4>
            <p class="description">{{ task.description || 'Aucune description' }}</p>
            <div class="task-meta">
              <span class="status" [class]="'status-' + task.status.toLowerCase()">
                {{ task.status }}
              </span>
              <span class="priority" [class]="'priority-' + task.priority.toLowerCase()">
                {{ task.priority }}
              </span>
            </div>
            <div class="task-info" *ngIf="task.dueDate">
              <span>Date d'échéance: {{ task.dueDate | date:'dd/MM/yyyy' }}</span>
            </div>
            <div class="task-info" *ngIf="task.endDate">
              <span>Date de fin: {{ task.endDate | date:'dd/MM/yyyy' }}</span>
            </div>
            
            <div class="assignment-section">
              <div class="current-assignment">
                <span class="label">Assigné à:</span>
                <span class="assigned-user">{{ getAssignedMemberName(task, group) }}</span>
              </div>
              
              <div class="assign-control" *ngIf="canAssignTask(group)">
                <label for="assign-{{ task.id }}">Assigner à:</label>
                <select 
                  id="assign-{{ task.id }}"
                  [(ngModel)]="assignmentValues[task.id!]"
                  class="assign-select"
                  [disabled]="assigning[task.id!]"
                >
                  <option value="">Sélectionner un membre</option>
                  <option *ngFor="let member of group.members" [value]="member.userId">
                    {{ member.userName || member.userEmail }} ({{ member.role }})
                  </option>
                </select>
                <button 
                  (click)="assignTask(task, group.project.id!)"
                  class="btn-assign"
                  [disabled]="!assignmentValues[task.id!] || assigning[task.id!]"
                >
                  <span *ngIf="assigning[task.id!]">Assignation...</span>
                  <span *ngIf="!assigning[task.id!]">Assigner</span>
                </button>
              </div>
            </div>
            
            <div *ngIf="assignmentError[task.id!]" class="alert alert-error">
              {{ assignmentError[task.id!] }}
            </div>
            <div *ngIf="assignmentSuccess[task.id!]" class="alert alert-success">
              {{ assignmentSuccess[task.id!] }}
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .task-list-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 1rem;
    }
    h2 {
      color: #2c3e50;
      margin-bottom: 1.5rem;
    }
    .project-group {
      margin-bottom: 2.5rem;
    }
    .project-title {
      color: #2c3e50;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid #3498db;
    }
    .tasks-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    .task-card {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: transform 0.2s, box-shadow 0.2s;
    }
    .task-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    }
    .task-card h4 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
    }
    .description {
      color: #7f8c8d;
      margin: 0.5rem 0;
      font-size: 0.9rem;
    }
    .task-meta {
      display: flex;
      gap: 0.5rem;
      margin: 1rem 0;
      flex-wrap: wrap;
    }
    .status, .priority {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.85rem;
      font-weight: 600;
    }
    .status-todo { background-color: #e8f4f8; color: #3498db; }
    .status-in_progress { background-color: #fff3cd; color: #f39c12; }
    .status-done { background-color: #d4edda; color: #27ae60; }
    .priority-low { background-color: #e8f4f8; color: #3498db; }
    .priority-medium { background-color: #fff3cd; color: #f39c12; }
    .priority-high { background-color: #ffeaa7; color: #e67e22; }
    .task-info {
      font-size: 0.85rem;
      color: #7f8c8d;
      margin-top: 0.5rem;
    }
    .assignment-section {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid #e0e0e0;
    }
    .current-assignment {
      margin-bottom: 0.75rem;
      font-size: 0.9rem;
    }
    .label {
      color: #7f8c8d;
      font-weight: 500;
    }
    .assigned-user {
      color: #2c3e50;
      font-weight: 600;
      margin-left: 0.5rem;
    }
    .assign-control {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .assign-control label {
      font-size: 0.875rem;
      color: #2c3e50;
      font-weight: 500;
    }
    .assign-select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.875rem;
      background-color: white;
      cursor: pointer;
    }
    .assign-select:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
    }
    .assign-select:disabled {
      background-color: #f8f9fa;
      cursor: not-allowed;
      opacity: 0.6;
    }
    .btn-assign {
      padding: 0.5rem 1rem;
      background-color: #27ae60;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 0.875rem;
      font-weight: 600;
      cursor: pointer;
      transition: background-color 0.3s;
    }
    .btn-assign:hover:not(:disabled) {
      background-color: #229954;
    }
    .btn-assign:disabled {
      background-color: #bdc3c7;
      cursor: not-allowed;
    }
    .alert {
      padding: 0.75rem;
      border-radius: 4px;
      margin-top: 0.5rem;
      font-size: 0.875rem;
    }
    .alert-error {
      background-color: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }
    .alert-success {
      background-color: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
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
export class TaskListComponent implements OnInit {
  taskGroups: TaskGroup[] = [];
  loading = true;
  error: string | null = null;
  currentUser: AuthResponse | null = null;
  assignmentValues: { [taskId: string]: string } = {};
  assigning: { [taskId: string]: boolean } = {};
  assignmentError: { [taskId: string]: string | null } = {};
  assignmentSuccess: { [taskId: string]: string | null } = {};

  constructor(
    private taskService: TaskService,
    private projectService: ProjectService,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.sessionService.getCurrentUser();
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.error = null;
    
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        const groups: TaskGroup[] = [];
        let loadedCount = 0;
        
        if (projects.length === 0) {
          this.taskGroups = [];
          this.loading = false;
          return;
        }
        
        projects.forEach((project) => {
          // Charger les tâches et les membres du projet en parallèle
          let tasksLoaded = false;
          let membersLoaded = false;
          let tasks: Task[] = [];
          let members: any[] = [];

          const checkComplete = () => {
            if (tasksLoaded && membersLoaded) {
              if (tasks.length > 0) {
                groups.push({ project, tasks, members });
              }
              loadedCount++;
              if (loadedCount === projects.length) {
                this.taskGroups = groups;
                this.loading = false;
              }
            }
          };

          this.taskService.getTasksByProject(project.id!).subscribe({
            next: (loadedTasks) => {
              tasks = loadedTasks;
              tasksLoaded = true;
              checkComplete();
            },
            error: (err) => {
              console.error(`Erreur lors du chargement des tâches pour le projet ${project.id}:`, err);
              tasksLoaded = true;
              checkComplete();
            }
          });

          this.projectService.getProjectMembers(project.id!).subscribe({
            next: (loadedMembers) => {
              members = loadedMembers;
              membersLoaded = true;
              checkComplete();
            },
            error: (err) => {
              console.error(`Erreur lors du chargement des membres pour le projet ${project.id}:`, err);
              membersLoaded = true;
              checkComplete();
            }
          });
        });
      },
      error: (err) => {
        console.error('Erreur lors du chargement des projets:', err);
        this.error = 'Impossible de charger les projets. Vérifiez que le backend est démarré.';
        this.loading = false;
      }
    });
  }

  getAssignedMemberName(task: Task, group: TaskGroup): string {
    if (!task.assignedUserId) {
      return 'Non assigné';
    }
    const member = group.members.find(m => m.userId === task.assignedUserId);
    if (member) {
      return member.userName || member.userEmail || 'Membre inconnu';
    }
    return 'Membre inconnu';
  }

  canAssignTask(group: TaskGroup): boolean {
    if (!this.currentUser || !group.members || group.members.length === 0) {
      return false;
    }
    // Vérifier si l'utilisateur actuel est ADMIN ou MEMBER du projet
    const currentUserMember = group.members.find(
      m => m.userId === this.currentUser!.userId
    );
    return currentUserMember !== undefined && 
           (currentUserMember.role === 'ADMIN' || currentUserMember.role === 'MEMBER');
  }

  assignTask(task: Task, projectId: string): void {
    const selectedUserId = this.assignmentValues[task.id!];
    if (!selectedUserId || !projectId) {
      return;
    }

    this.assigning[task.id!] = true;
    this.assignmentError[task.id!] = null;
    this.assignmentSuccess[task.id!] = null;

    this.taskService.assignTask(task.id!, projectId, selectedUserId).subscribe({
      next: () => {
        this.assignmentSuccess[task.id!] = 'Tâche assignée avec succès';
        this.assignmentValues[task.id!] = '';
        this.assigning[task.id!] = false;
        // Recharger les tâches pour mettre à jour l'affichage
        setTimeout(() => {
          this.loadTasks();
        }, 1000);
      },
      error: (err) => {
        console.error('Erreur lors de l\'assignation:', err);
        this.assignmentError[task.id!] = err.error && typeof err.error === 'string' 
          ? err.error 
          : 'Erreur lors de l\'assignation de la tâche.';
        this.assigning[task.id!] = false;
      }
    });
  }
}
