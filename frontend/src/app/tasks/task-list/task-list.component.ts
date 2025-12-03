import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="task-list-container">
      <h2>Liste des Tâches</h2>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="!loading && !error && tasks.length === 0" class="empty">Aucune tâche trouvée.</div>
      <div *ngIf="!loading && !error && tasks.length > 0" class="tasks-grid">
        <div *ngFor="let task of tasks" class="task-card">
          <h3>{{ task.title }}</h3>
          <p class="description">{{ task.description || 'Aucune description' }}</p>
          <div class="task-meta">
            <span class="status" [class]="'status-' + task.status.toLowerCase()">
              {{ task.status }}
            </span>
            <span class="priority" [class]="'priority-' + task.priority.toLowerCase()">
              {{ task.priority }}
            </span>
          </div>
          <div class="task-info" *ngIf="task.project">
            <span>Projet: {{ task.project.name }}</span>
          </div>
          <div class="task-info" *ngIf="task.assignedUser">
            <span>Assigné à: {{ task.assignedUser.firstName }} {{ task.assignedUser.lastName }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .task-list-container {
      max-width: 1200px;
      margin: 0 auto;
    }
    h2 {
      color: #2c3e50;
      margin-bottom: 1.5rem;
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
    .task-card h3 {
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
    .status-cancelled { background-color: #f8d7da; color: #e74c3c; }
    .priority-low { background-color: #e8f4f8; color: #3498db; }
    .priority-medium { background-color: #fff3cd; color: #f39c12; }
    .priority-high { background-color: #ffeaa7; color: #e67e22; }
    .priority-urgent { background-color: #f8d7da; color: #e74c3c; }
    .task-info {
      font-size: 0.85rem;
      color: #7f8c8d;
      margin-top: 0.5rem;
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
  tasks: Task[] = [];
  loading = true;
  error: string | null = null;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.error = null;
    this.taskService.getAllTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des tâches:', err);
        this.error = 'Impossible de charger les tâches. Vérifiez que le backend est démarré.';
        this.loading = false;
      }
    });
  }
}

