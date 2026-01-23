import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { SessionService } from '../../services/session.service';
import { Task, TaskStatus, TaskPriority, TaskHistory } from '../../models/task.model';
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
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
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
            <!-- Onglets -->
            <div class="tabs">
              <button 
                class="tab-btn" 
                [class.active]="activeTab[task.id!] === 'details'"
                (click)="setActiveTab(task.id!, 'details')"
              >
                Détails
              </button>
              <button 
                class="tab-btn" 
                [class.active]="activeTab[task.id!] === 'edit'"
                (click)="setActiveTab(task.id!, 'edit')"
                *ngIf="canEditTask(group)"
              >
                Modifier
              </button>
              <button 
                class="tab-btn" 
                [class.active]="activeTab[task.id!] === 'history'"
                (click)="setActiveTab(task.id!, 'history'); loadTaskHistory(task.id!)"
              >
                Historique
              </button>
            </div>

            <!-- Onglet Détails -->
            <div *ngIf="activeTab[task.id!] === 'details'" class="tab-content">
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

            <!-- Onglet Modifier -->
            <div *ngIf="activeTab[task.id!] === 'edit' && canEditTask(group) && editForms[task.id!]" class="tab-content">
              <form [formGroup]="editForms[task.id!]!" (ngSubmit)="updateTask(task, group.project.id!)">
                <div class="form-group">
                  <label for="name-{{ task.id }}">Nom *</label>
                  <input
                    type="text"
                    id="name-{{ task.id }}"
                    formControlName="name"
                    class="form-control"
                    [class.error]="isFieldInvalid(task.id!, 'name')"
                  />
                  <div *ngIf="isFieldInvalid(task.id!, 'name')" class="error-message">
                    Le nom est requis
                  </div>
                </div>

                <div class="form-group">
                  <label for="description-{{ task.id }}">Description</label>
                  <textarea
                    id="description-{{ task.id }}"
                    formControlName="description"
                    class="form-control"
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label for="status-{{ task.id }}">Statut *</label>
                  <select id="status-{{ task.id }}" formControlName="status" class="form-control">
                    <option [value]="TaskStatus.TODO">TODO</option>
                    <option [value]="TaskStatus.IN_PROGRESS">IN_PROGRESS</option>
                    <option [value]="TaskStatus.DONE">DONE</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="priority-{{ task.id }}">Priorité *</label>
                  <select id="priority-{{ task.id }}" formControlName="priority" class="form-control">
                    <option [value]="TaskPriority.LOW">LOW</option>
                    <option [value]="TaskPriority.MEDIUM">MEDIUM</option>
                    <option [value]="TaskPriority.HIGH">HIGH</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="dueDate-{{ task.id }}">Date d'échéance</label>
                  <input
                    type="date"
                    id="dueDate-{{ task.id }}"
                    formControlName="dueDate"
                    class="form-control"
                  />
                </div>

                <div class="form-group">
                  <label for="endDate-{{ task.id }}">Date de fin</label>
                  <input
                    type="date"
                    id="endDate-{{ task.id }}"
                    formControlName="endDate"
                    class="form-control"
                  />
                </div>

                <div *ngIf="updateError[task.id!]" class="alert alert-error">
                  {{ updateError[task.id!] }}
                </div>
                <div *ngIf="updateSuccess[task.id!]" class="alert alert-success">
                  {{ updateSuccess[task.id!] }}
                </div>

                <div class="form-actions">
                  <button type="button" class="btn btn-secondary" (click)="cancelEdit(task.id!)">
                    Annuler
                  </button>
                  <button type="submit" class="btn btn-primary" [disabled]="!editForms[task.id!] || editForms[task.id!].invalid || updating[task.id!]">
                    <span *ngIf="updating[task.id!]">Mise à jour...</span>
                    <span *ngIf="!updating[task.id!]">Enregistrer</span>
                  </button>
                </div>
              </form>
            </div>

            <!-- Onglet Historique -->
            <div *ngIf="activeTab[task.id!] === 'history'" class="tab-content">
              <div *ngIf="loadingHistory[task.id!]" class="loading-small">Chargement de l'historique...</div>
              <div *ngIf="!loadingHistory[task.id!] && historyError[task.id!]" class="alert alert-error">
                {{ historyError[task.id!] }}
              </div>
              <div *ngIf="!loadingHistory[task.id!] && !historyError[task.id!] && (!taskHistory[task.id!] || taskHistory[task.id!].length === 0)" class="empty-small">
                Aucun historique disponible
              </div>
              <div *ngIf="!loadingHistory[task.id!] && !historyError[task.id!] && taskHistory[task.id!] && taskHistory[task.id!].length > 0" class="history-list">
                <div *ngFor="let entry of taskHistory[task.id!]" class="history-item">
                  <div class="history-header">
                    <span class="history-field">{{ getFieldDisplayName(entry.fieldName) }}</span>
                    <span class="history-date">{{ entry.modifiedAt | date:'dd/MM/yyyy HH:mm' }}</span>
                  </div>
                  <div class="history-changes">
                    <span class="old-value" *ngIf="entry.oldValue">
                      <strong>Ancien:</strong> {{ entry.oldValue }}
                    </span>
                    <span class="new-value" *ngIf="entry.newValue">
                      <strong>Nouveau:</strong> {{ entry.newValue }}
                    </span>
                  </div>
                </div>
              </div>
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
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
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
    .tabs {
      display: flex;
      gap: 0.5rem;
      margin-bottom: 1rem;
      border-bottom: 2px solid #e0e0e0;
    }
    .tab-btn {
      padding: 0.5rem 1rem;
      background: none;
      border: none;
      border-bottom: 2px solid transparent;
      cursor: pointer;
      font-size: 0.875rem;
      color: #7f8c8d;
      transition: all 0.3s;
      margin-bottom: -2px;
    }
    .tab-btn:hover {
      color: #3498db;
    }
    .tab-btn.active {
      color: #3498db;
      border-bottom-color: #3498db;
      font-weight: 600;
    }
    .tab-content {
      min-height: 200px;
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
    .form-group {
      margin-bottom: 1rem;
    }
    .form-group label {
      display: block;
      margin-bottom: 0.25rem;
      color: #2c3e50;
      font-size: 0.875rem;
      font-weight: 500;
    }
    .form-control {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.875rem;
      font-family: inherit;
    }
    .form-control:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
    }
    .form-control.error {
      border-color: #e74c3c;
    }
    textarea.form-control {
      resize: vertical;
    }
    .error-message {
      color: #e74c3c;
      font-size: 0.75rem;
      margin-top: 0.25rem;
    }
    .form-actions {
      display: flex;
      gap: 0.5rem;
      justify-content: flex-end;
      margin-top: 1rem;
    }
    .btn {
      padding: 0.5rem 1rem;
      border: none;
      border-radius: 4px;
      font-size: 0.875rem;
      font-weight: 600;
      cursor: pointer;
      transition: background-color 0.3s;
    }
    .btn-primary {
      background-color: #3498db;
      color: white;
    }
    .btn-primary:hover:not(:disabled) {
      background-color: #2980b9;
    }
    .btn-primary:disabled {
      background-color: #bdc3c7;
      cursor: not-allowed;
    }
    .btn-secondary {
      background-color: #95a5a6;
      color: white;
    }
    .btn-secondary:hover {
      background-color: #7f8c8d;
    }
    .history-list {
      max-height: 300px;
      overflow-y: auto;
    }
    .history-item {
      padding: 0.75rem;
      margin-bottom: 0.5rem;
      background-color: #f8f9fa;
      border-radius: 4px;
      border-left: 3px solid #3498db;
    }
    .history-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 0.5rem;
    }
    .history-field {
      font-weight: 600;
      color: #2c3e50;
      text-transform: capitalize;
    }
    .history-date {
      font-size: 0.75rem;
      color: #7f8c8d;
    }
    .history-changes {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      font-size: 0.875rem;
    }
    .old-value {
      color: #e74c3c;
    }
    .new-value {
      color: #27ae60;
    }
    .loading-small, .empty-small {
      text-align: center;
      padding: 1rem;
      color: #7f8c8d;
      font-size: 0.875rem;
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
  
  activeTab: { [taskId: string]: string } = {};
  editForms: { [taskId: string]: FormGroup } = {};
  updating: { [taskId: string]: boolean } = {};
  updateError: { [taskId: string]: string | null } = {};
  updateSuccess: { [taskId: string]: string | null } = {};
  
  taskHistory: { [taskId: string]: TaskHistory[] } = {};
  loadingHistory: { [taskId: string]: boolean } = {};
  historyError: { [taskId: string]: string | null } = {};

  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(
    private taskService: TaskService,
    private projectService: ProjectService,
    private sessionService: SessionService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.currentUser = this.sessionService.getCurrentUser();
    this.loadTasks();
  }

  setActiveTab(taskId: string, tab: string): void {
    if (!taskId) return;
    this.activeTab[taskId] = tab;
    if (tab === 'edit' && !this.editForms[taskId]) {
      this.initEditForm(taskId);
    }
  }

  initEditForm(taskId: string): void {
    if (!taskId) return;
    const task = this.findTaskById(taskId);
    if (!task) return;

    this.editForms[taskId] = this.fb.group({
      name: [task.name, Validators.required],
      description: [task.description || ''],
      status: [task.status, Validators.required],
      priority: [task.priority, Validators.required],
      dueDate: [task.dueDate ? task.dueDate.split('T')[0] : ''],
      endDate: [task.endDate ? task.endDate.split('T')[0] : '']
    });
  }

  findTaskById(taskId: string): Task | null {
    for (const group of this.taskGroups) {
      const task = group.tasks.find(t => t.id === taskId);
      if (task) return task;
    }
    return null;
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
          let tasksLoaded = false;
          let membersLoaded = false;
          let tasks: Task[] = [];
          let members: any[] = [];

          const checkComplete = () => {
            if (tasksLoaded && membersLoaded) {
              if (tasks.length > 0) {
                groups.push({ project, tasks, members });
            
                tasks.forEach(task => {
                  if (!this.activeTab[task.id!]) {
                    this.activeTab[task.id!] = 'details';
                  }
                });
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
              if (err.status === 403) {
                tasksLoaded = true;
                checkComplete();
              } else {
                console.error(`Erreur lors du chargement des tâches pour le projet ${project.id}:`, err);
                tasksLoaded = true;
                checkComplete();
              }
            }
          });

          this.projectService.getProjectMembers(project.id!).subscribe({
            next: (loadedMembers) => {
              members = loadedMembers;
              membersLoaded = true;
              checkComplete();
            },
            error: (err) => {
              if (err.status === 403) {
                membersLoaded = true;
                checkComplete();
              } else {
                console.error(`Erreur lors du chargement des membres pour le projet ${project.id}:`, err);
                membersLoaded = true;
                checkComplete();
              }
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
    const currentUserMember = group.members.find(
      m => m.userId === this.currentUser!.userId
    );
    return currentUserMember !== undefined && 
           (currentUserMember.role === 'ADMIN' || currentUserMember.role === 'MEMBER');
  }

  canEditTask(group: TaskGroup): boolean {
    return this.canAssignTask(group);
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
      next: (updatedTask) => {
        this.assignmentSuccess[task.id!] = 'Tâche assignée avec succès';
        this.assignmentValues[task.id!] = '';
        this.assigning[task.id!] = false;
        this.reloadTasksForProject(projectId, () => {
          if (task.id) {
            this.reloadTaskHistory(task.id);
          }
        });
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

  isFieldInvalid(taskId: string, fieldName: string): boolean {
    const form = this.editForms[taskId];
    if (!form) return false;
    const field = form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  updateTask(task: Task, projectId: string): void {
    if (!task.id || !this.editForms[task.id] || this.editForms[task.id].invalid) {
      return;
    }

    this.updating[task.id] = true;
    this.updateError[task.id] = null;
    this.updateSuccess[task.id] = null;

    const formValue = this.editForms[task.id].value;
    const updatedTask: Task = {
      ...task,
      name: formValue.name,
      description: formValue.description || undefined,
      status: formValue.status,
      priority: formValue.priority,
      dueDate: formValue.dueDate || undefined,
      endDate: formValue.endDate || undefined
    };

    if (!task.id) return;
    this.taskService.updateTask(task.id, updatedTask).subscribe({
      next: (updatedTaskResponse) => {
        if (task.id) {
          const taskId = task.id;
          this.updateSuccess[taskId] = 'Tâche mise à jour avec succès';
          this.updating[taskId] = false;
          const projectId = this.findProjectIdForTask(taskId);
          if (projectId) {
            this.reloadTasksForProject(projectId, () => {
              this.reloadTaskHistory(taskId);
              this.setActiveTab(taskId, 'details');
            });
          } else {
            this.reloadTaskHistory(taskId);
            this.setActiveTab(taskId, 'details');
          }
        }
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour:', err);
        if (task.id) {
          this.updateError[task.id] = err.error && typeof err.error === 'string' 
            ? err.error 
            : 'Erreur lors de la mise à jour de la tâche.';
          this.updating[task.id] = false;
        }
      }
    });
  }

  cancelEdit(taskId: string): void {
    if (!taskId) return;
    delete this.editForms[taskId];
    this.setActiveTab(taskId, 'details');
  }

  loadTaskHistory(taskId: string, forceReload: boolean = false): void {
    if (!taskId) return;
    if (this.taskHistory[taskId] && !forceReload) {
      return;
    }

    this.loadingHistory[taskId] = true;
    this.historyError[taskId] = null;
    this.taskService.getTaskHistory(taskId).subscribe({
      next: (history) => {
        this.taskHistory[taskId] = history;
        this.loadingHistory[taskId] = false;
        this.historyError[taskId] = null;
      },
      error: (err) => {
        console.error('Erreur lors du chargement de l\'historique:', err);
        this.loadingHistory[taskId] = false;
        if (err.status === 403) {
          this.historyError[taskId] = 'Vous n\'avez pas les permissions pour visualiser l\'historique de cette tâche.';
        } else if (err.status === 404) {
          this.historyError[taskId] = 'Tâche introuvable.';
        } else {
          this.historyError[taskId] = err.error && typeof err.error === 'string' 
            ? err.error 
            : 'Erreur lors du chargement de l\'historique.';
        }
        this.taskHistory[taskId] = [];
      }
    });
  }

  reloadTaskHistory(taskId: string): void {
    if (!taskId) return;
    delete this.taskHistory[taskId];
    delete this.historyError[taskId];
    this.loadTaskHistory(taskId, true);
  }

  findProjectIdForTask(taskId: string): string | null {
    for (const group of this.taskGroups) {
      const task = group.tasks.find(t => t.id === taskId);
      if (task) {
        return group.project.id || null;
      }
    }
    return null;
  }

  updateTaskInList(updatedTask: Task): void {
    if (!updatedTask.id) return;
    
    for (const group of this.taskGroups) {
      const taskIndex = group.tasks.findIndex(t => t.id === updatedTask.id);
      if (taskIndex !== -1) {
        group.tasks[taskIndex] = { ...updatedTask };
        break;
      }
    }
  }

  reloadTasksForProject(projectId: string, callback?: () => void): void {
    if (!projectId) return;
    
    const group = this.taskGroups.find(g => g.project.id === projectId);
    if (!group) return;
    
    this.taskService.getTasksByProject(projectId).subscribe({
      next: (tasks) => {
        group.tasks = tasks;
        tasks.forEach(task => {
          if (task.id && !this.activeTab[task.id]) {
            this.activeTab[task.id] = 'details';
          }
        });
        if (callback) {
          callback();
        }
      },
      error: (err) => {
        console.error(`Erreur lors du rechargement des tâches pour le projet ${projectId}:`, err);
        if (callback) {
          callback();
        }
      }
    });
  }

  getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      'name': 'Nom',
      'description': 'Description',
      'status': 'Statut',
      'priority': 'Priorité',
      'dueDate': 'Date d\'échéance',
      'endDate': 'Date de fin',
      'projectMembers': 'Assignation'
    };
    return fieldNames[fieldName] || fieldName;
  }
}
