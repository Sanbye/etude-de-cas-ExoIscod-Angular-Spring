import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { InviteMemberRequest } from '../../models/invite-member.model';
import { Role, TaskPriority } from '../../models/task.model';
import { SessionService } from '../../services/session.service';
import { AuthResponse } from '../../models/auth.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FormsModule],
  template: `
    <div class="project-detail-container">
      <div class="header-actions">
        <button (click)="goBack()" class="btn-back">← Retour</button>
        <h2>Détails du Projet</h2>
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>

      <div *ngIf="!loading && !error && project" class="project-detail-card">
        <div class="project-info">
          <h3>{{ project.name }}</h3>
          <p class="description">{{ project.description || 'Aucune description' }}</p>
          <div class="project-meta" *ngIf="project.startingDate">
            <span class="date">Date de début: {{ project.startingDate | date:'dd/MM/yyyy' }}</span>
          </div>
        </div>

        <div class="invite-section" *ngIf="isProjectAdmin">
          <h4>Inviter un membre</h4>
          <form [formGroup]="inviteForm" (ngSubmit)="onInviteMember()">
            <div class="form-group">
              <label for="email">Adresse e-mail *</label>
              <input
                type="email"
                id="email"
                formControlName="email"
                class="form-control"
                [class.error]="isFieldInvalid('email')"
                placeholder="email@example.com"
              />
              <div *ngIf="isFieldInvalid('email')" class="error-message">
                <span *ngIf="inviteForm.get('email')?.errors?.['required']">L'email est requis</span>
                <span *ngIf="inviteForm.get('email')?.errors?.['email']">Format d'email invalide</span>
              </div>
            </div>

            <div class="form-group">
              <label for="role">Rôle *</label>
              <select
                id="role"
                formControlName="role"
                class="form-control"
                [class.error]="isFieldInvalid('role')"
              >
                <option value="">Sélectionner un rôle</option>
                <option [value]="Role.ADMIN">Administrateur</option>
                <option [value]="Role.MEMBER">Membre</option>
                <option [value]="Role.OBSERVER">Observateur</option>
              </select>
              <div *ngIf="isFieldInvalid('role')" class="error-message">
                Le rôle est requis
              </div>
            </div>

            <div *ngIf="inviteError" class="alert alert-error">
              {{ inviteError }}
            </div>

            <div *ngIf="inviteSuccess" class="alert alert-success">
              {{ inviteSuccess }}
            </div>

            <button type="submit" class="btn btn-primary" [disabled]="inviteForm.invalid || inviting">
              <span *ngIf="inviting">Invitation en cours...</span>
              <span *ngIf="!inviting">Inviter</span>
            </button>
          </form>
        </div>

        <div class="tasks-section" *ngIf="isProjectMember">
          <h4>Créer une tâche</h4>
          <form [formGroup]="taskForm" (ngSubmit)="onCreateTask()">
            <div class="form-group">
              <label for="taskName">Nom de la tâche *</label>
              <input
                type="text"
                id="taskName"
                formControlName="name"
                class="form-control"
                [class.error]="isTaskFieldInvalid('name')"
                placeholder="Nom de la tâche"
              />
              <div *ngIf="isTaskFieldInvalid('name')" class="error-message">
                Le nom de la tâche est requis
              </div>
            </div>

            <div class="form-group">
              <label for="taskDescription">Description</label>
              <textarea
                id="taskDescription"
                formControlName="description"
                class="form-control"
                rows="3"
                placeholder="Description de la tâche"
              ></textarea>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="taskDueDate">Date d'échéance</label>
                <input
                  type="date"
                  id="taskDueDate"
                  formControlName="dueDate"
                  class="form-control"
                />
              </div>

              <div class="form-group">
                <label for="taskPriority">Priorité *</label>
                <select
                  id="taskPriority"
                  formControlName="priority"
                  class="form-control"
                  [class.error]="isTaskFieldInvalid('priority')"
                >
                  <option value="">Sélectionner une priorité</option>
                  <option [value]="TaskPriority.LOW">Basse</option>
                  <option [value]="TaskPriority.MEDIUM">Moyenne</option>
                  <option [value]="TaskPriority.HIGH">Haute</option>
                </select>
                <div *ngIf="isTaskFieldInvalid('priority')" class="error-message">
                  La priorité est requise
                </div>
              </div>
            </div>

            <div *ngIf="taskError" class="alert alert-error">
              {{ taskError }}
            </div>

            <div *ngIf="taskSuccess" class="alert alert-success">
              {{ taskSuccess }}
            </div>

            <button type="submit" class="btn btn-primary" [disabled]="taskForm.invalid || creatingTask">
              <span *ngIf="creatingTask">Création en cours...</span>
              <span *ngIf="!creatingTask">Créer la tâche</span>
            </button>
          </form>
        </div>

        <div class="members-section">
          <h4>Membres du projet</h4>
          <div *ngIf="loadingMembers" class="loading">Chargement des membres...</div>
          <div *ngIf="!loadingMembers && members.length === 0" class="empty">Aucun membre pour le moment</div>
          <div *ngIf="!loadingMembers && members.length > 0" class="members-list">
            <div *ngFor="let member of members" class="member-item">
              <div class="member-info">
                <span class="member-name">{{ getMemberName(member) }}</span>
                <span class="member-email" *ngIf="getMemberEmail(member)">{{ getMemberEmail(member) }}</span>
              </div>
              <div class="member-role-section">
                <!-- Mode affichage -->
                <div *ngIf="!editingRoles[member.userId]" class="role-display">
                  <span class="member-role" [class]="'role-' + member.role.toLowerCase()">
                    {{ getRoleLabel(member.role) }}
                  </span>
                  <button
                    *ngIf="isProjectAdmin && !isCurrentUser(member)"
                    (click)="startEditingRole(member)"
                    class="btn-edit-role"
                    [disabled]="updatingRoles[member.userId]"
                  >
                    Modifier
                  </button>
                </div>
                
                <!-- Mode édition -->
                <div *ngIf="editingRoles[member.userId]" class="role-edit">
                  <select
                    [(ngModel)]="editingRoleValues[member.userId]"
                    class="role-select"
                    [disabled]="updatingRoles[member.userId]"
                  >
                    <option [value]="Role.ADMIN">Administrateur</option>
                    <option [value]="Role.MEMBER">Membre</option>
                    <option [value]="Role.OBSERVER">Observateur</option>
                  </select>
                  <div class="edit-actions">
                    <button
                      (click)="saveRoleChange(member)"
                      class="btn-save-role"
                      [disabled]="updatingRoles[member.userId]"
                    >
                      Sauvegarder
                    </button>
                    <button
                      (click)="cancelEditingRole(member.userId)"
                      class="btn-cancel-role"
                      [disabled]="updatingRoles[member.userId]"
                    >
                      Annuler
                    </button>
                  </div>
                  <span *ngIf="updatingRoles[member.userId]" class="updating-indicator">
                    Mise à jour...
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .project-detail-container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .btn-back {
      background-color: #95a5a6;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.9rem;
      transition: background-color 0.3s;
    }

    .btn-back:hover {
      background-color: #7f8c8d;
    }

    h2 {
      color: #2c3e50;
      margin: 0;
    }

    .project-detail-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      padding: 2rem;
    }

    .project-info {
      margin-bottom: 2rem;
      padding-bottom: 2rem;
      border-bottom: 2px solid #e0e0e0;
    }

    .invite-section,
    .tasks-section,
    .members-section {
      margin-bottom: 2.5rem;
      padding: 1.5rem;
      background-color: #ffffff;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }

    .invite-section {
      border-left: 4px solid #3498db;
    }

    .tasks-section {
      border-left: 4px solid #27ae60;
    }

    .members-section {
      border-left: 4px solid #9b59b6;
    }

    .invite-section h4,
    .tasks-section h4,
    .members-section h4 {
      margin-top: 0;
      margin-bottom: 1.5rem;
      padding-bottom: 0.75rem;
      border-bottom: 2px solid #f0f0f0;
      color: #2c3e50;
      font-size: 1.25rem;
      font-weight: 600;
    }

    .invite-section h4 {
      color: #3498db;
    }

    .tasks-section h4 {
      color: #27ae60;
    }

    .members-section h4 {
      color: #9b59b6;
    }

    .project-info h3 {
      color: #2c3e50;
      margin-bottom: 0.5rem;
    }

    .description {
      color: #7f8c8d;
      margin-bottom: 1rem;
    }

    .project-meta {
      margin-top: 1rem;
    }

    .date {
      color: #7f8c8d;
      font-size: 0.9rem;
    }


    .form-group {
      margin-bottom: 1.5rem;
    }

    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #2c3e50;
      font-weight: 500;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
      font-family: inherit;
      transition: border-color 0.3s;
      box-sizing: border-box;
    }

    .form-control:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
    }

    .form-control.error {
      border-color: #e74c3c;
    }

    .error-message {
      color: #e74c3c;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }

    .alert {
      padding: 0.75rem;
      border-radius: 4px;
      margin-bottom: 1rem;
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

    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
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

    .members-list {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .member-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem;
      background-color: #f8f9fa;
      border-radius: 4px;
    }

    .member-info {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .member-name {
      color: #2c3e50;
      font-weight: 600;
      font-size: 1rem;
    }

    .member-email {
      color: #7f8c8d;
      font-size: 0.875rem;
    }

    .member-role {
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.875rem;
      font-weight: 600;
    }

    .role-admin {
      background-color: #e74c3c;
      color: white;
    }

    .role-member {
      background-color: #3498db;
      color: white;
    }

    .role-observer {
      background-color: #95a5a6;
      color: white;
    }

    .member-role-section {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .role-display {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }

    .role-edit {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      flex-wrap: wrap;
    }

    .btn-edit-role {
      padding: 0.25rem 0.75rem;
      background-color: #3498db;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .btn-edit-role:hover:not(:disabled) {
      background-color: #2980b9;
    }

    .btn-edit-role:disabled {
      background-color: #bdc3c7;
      cursor: not-allowed;
    }

    .edit-actions {
      display: flex;
      gap: 0.5rem;
    }

    .btn-save-role, .btn-cancel-role {
      padding: 0.25rem 0.75rem;
      border: none;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .btn-save-role {
      background-color: #27ae60;
      color: white;
    }

    .btn-save-role:hover:not(:disabled) {
      background-color: #229954;
    }

    .btn-cancel-role {
      background-color: #95a5a6;
      color: white;
    }

    .btn-cancel-role:hover:not(:disabled) {
      background-color: #7f8c8d;
    }

    .btn-save-role:disabled, .btn-cancel-role:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .role-select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.875rem;
      background-color: white;
      cursor: pointer;
      transition: border-color 0.3s;
    }

    .role-select:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
    }

    .role-select:disabled {
      background-color: #f8f9fa;
      cursor: not-allowed;
      opacity: 0.6;
    }

    .updating-indicator {
      font-size: 0.75rem;
      color: #7f8c8d;
      font-style: italic;
    }

    .loading, .error, .empty {
      text-align: center;
      padding: 2rem;
      color: #7f8c8d;
    }

    .error {
      color: #e74c3c;
    }
  `]
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  members: any[] = [];
  loading = false;
  loadingMembers = false;
  error: string | null = null;
  inviteForm!: FormGroup;
  inviting = false;
  inviteError: string | null = null;
  inviteSuccess: string | null = null;
  Role = Role;
  TaskPriority = TaskPriority;
  currentUser: AuthResponse | null = null;
  isProjectAdmin = false;
  isProjectMember = false;
  updatingRoles: { [userId: string]: boolean } = {};
  editingRoles: { [userId: string]: boolean } = {};
  editingRoleValues: { [userId: string]: Role } = {};
  taskForm!: FormGroup;
  creatingTask = false;
  taskError: string | null = null;
  taskSuccess: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private fb: FormBuilder,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.sessionService.getCurrentUser();
    const projectId = this.route.snapshot.paramMap.get('id');
    if (projectId) {
      this.loadProject(projectId);
      this.loadMembers(projectId);
    }

    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required]
    });

    this.taskForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      dueDate: [''],
      priority: ['', Validators.required]
    });
  }

  loadProject(id: string): void {
    this.loading = true;
    this.error = null;
    this.projectService.getProjectById(id).subscribe({
      next: (project) => {
        this.project = project;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement du projet';
        this.loading = false;
      }
    });
  }

  loadMembers(projectId: string): void {
    this.loadingMembers = true;
    this.isProjectAdmin = false; // Réinitialiser avant de charger
    this.isProjectMember = false; // Réinitialiser avant de charger
    this.members = []; // Réinitialiser la liste
    this.projectService.getProjectMembers(projectId).subscribe({
      next: (members) => {
        console.log('Membres reçus:', members);
        this.members = members || [];
        this.checkIfCurrentUserIsAdmin();
        this.loadingMembers = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des membres:', err);
        console.error('Status:', err.status);
        console.error('Message:', err.message);
        console.error('Error body:', err.error);
        this.loadingMembers = false;
        this.isProjectAdmin = false;
        this.isProjectMember = false;
        this.members = [];
      }
    });
  }

  checkIfCurrentUserIsAdmin(): void {
    this.isProjectAdmin = false; // Réinitialiser à false par défaut
    this.isProjectMember = false; // Réinitialiser à false par défaut
    
    if (!this.currentUser || !this.currentUser.userId) {
      return;
    }
    
    if (!this.members || this.members.length === 0) {
      return;
    }
    
    const currentUserId = String(this.currentUser.userId).trim().toLowerCase();
    
    this.isProjectAdmin = this.members.some(
      (member) => {
        const memberUserId = String(member.userId || member.user_id || '').trim().toLowerCase();
        return memberUserId === currentUserId && String(member.role) === String(Role.ADMIN);
      }
    );

    // Vérifier si l'utilisateur est membre (ADMIN ou MEMBER) du projet
    this.isProjectMember = this.members.some(
      (member) => {
        const memberUserId = String(member.userId || member.user_id || '').trim().toLowerCase();
        return memberUserId === currentUserId && 
               (String(member.role) === String(Role.ADMIN) || String(member.role) === String(Role.MEMBER));
      }
    );
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.inviteForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  isTaskFieldInvalid(fieldName: string): boolean {
    const field = this.taskForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onInviteMember(): void {
    if (this.inviteForm.valid && this.project) {
      this.inviting = true;
      this.inviteError = null;
      this.inviteSuccess = null;

      const request: InviteMemberRequest = {
        email: this.inviteForm.value.email,
        role: this.inviteForm.value.role as Role
      };

      this.projectService.inviteMember(this.project.id!, request).subscribe({
        next: () => {
          this.inviteSuccess = 'Membre invité avec succès !';
          this.inviteForm.reset();
          this.loadMembers(this.project!.id!);
          this.inviting = false;
          setTimeout(() => {
            this.inviteSuccess = null;
          }, 3000);
        },
        error: (err) => {
          this.inviting = false;
          if (err.error && typeof err.error === 'string') {
            this.inviteError = err.error;
          } else {
            this.inviteError = 'Erreur lors de l\'invitation. Veuillez réessayer.';
          }
        }
      });
    }
  }

  getMemberName(member: any): string {
    // Utiliser userName du DTO
    if (member.userName) {
      return member.userName;
    }
    // Sinon, retourner email ou un identifiant par défaut
    return member.userEmail || 'Utilisateur inconnu';
  }

  getMemberEmail(member: any): string {
    // Utiliser userEmail du DTO
    if (member.userEmail) {
      return member.userEmail;
    }
    // Si pas d'email, ne rien afficher
    return '';
  }

  isCurrentUser(member: any): boolean {
    if (!this.currentUser || !this.currentUser.userId) {
      return false;
    }
    const currentUserId = String(this.currentUser.userId).trim().toLowerCase();
    const memberUserId = String(member.userId || member.user_id || '').trim().toLowerCase();
    return currentUserId === memberUserId;
  }

  getRoleLabel(role: string): string {
    switch (role) {
      case Role.ADMIN:
        return 'Administrateur';
      case Role.MEMBER:
        return 'Membre';
      case Role.OBSERVER:
        return 'Observateur';
      default:
        return role;
    }
  }

  startEditingRole(member: any): void {
    this.editingRoles[member.userId] = true;
    this.editingRoleValues[member.userId] = member.role as Role;
  }

  cancelEditingRole(userId: string): void {
    this.editingRoles[userId] = false;
    delete this.editingRoleValues[userId];
  }

  saveRoleChange(member: any): void {
    if (!this.project || !member.userId) {
      return;
    }

    const newRole = this.editingRoleValues[member.userId];
    
    if (newRole === member.role) {
      this.cancelEditingRole(member.userId);
      return;
    }

    this.updatingRoles[member.userId] = true;

    this.projectService.updateMemberRole(this.project.id!, member.userId, newRole).subscribe({
      next: () => {
        // Mettre à jour le rôle localement
        member.role = newRole;
        this.updatingRoles[member.userId] = false;
        this.cancelEditingRole(member.userId);
        // Recharger les membres pour s'assurer de la cohérence
        this.loadMembers(this.project!.id!);
      },
      error: (err) => {
        this.updatingRoles[member.userId] = false;
        alert(err.error && typeof err.error === 'string' ? err.error : 'Erreur lors de la mise à jour du rôle. Veuillez réessayer.');
      }
    });
  }

  onCreateTask(): void {
    if (this.taskForm.valid && this.project) {
      this.creatingTask = true;
      this.taskError = null;
      this.taskSuccess = null;

      const taskData = {
        name: this.taskForm.value.name,
        description: this.taskForm.value.description || undefined,
        dueDate: this.taskForm.value.dueDate || undefined,
        priority: this.taskForm.value.priority as TaskPriority
      };

      this.taskService.createTask(this.project.id!, taskData).subscribe({
        next: () => {
          this.taskSuccess = 'Tâche créée avec succès !';
          this.taskForm.reset();
          this.creatingTask = false;
          setTimeout(() => {
            this.taskSuccess = null;
          }, 3000);
        },
        error: (err: any) => {
          this.creatingTask = false;
          if (err.error && typeof err.error === 'string') {
            this.taskError = err.error;
          } else {
            this.taskError = 'Erreur lors de la création de la tâche. Veuillez réessayer.';
          }
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/projects']);
  }
}
