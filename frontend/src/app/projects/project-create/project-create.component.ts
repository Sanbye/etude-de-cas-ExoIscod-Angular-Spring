import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-project-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="project-create-container">
      <div class="project-create-card">
        <h2>Créer un nouveau projet</h2>
        <form [formGroup]="projectForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="name">Nom du projet *</label>
            <input
              type="text"
              id="name"
              formControlName="name"
              class="form-control"
              [class.error]="isFieldInvalid('name')"
              placeholder="Entrez le nom du projet"
            />
            <div *ngIf="isFieldInvalid('name')" class="error-message">
              <span *ngIf="projectForm.get('name')?.errors?.['required']">Le nom du projet est requis</span>
              <span *ngIf="projectForm.get('name')?.errors?.['minlength']">Le nom doit contenir au moins 3 caractères</span>
            </div>
          </div>

          <div class="form-group">
            <label for="description">Description</label>
            <textarea
              id="description"
              formControlName="description"
              class="form-control"
              rows="4"
              placeholder="Décrivez le projet (optionnel)"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="startingDate">Date de début</label>
            <input
              type="date"
              id="startingDate"
              formControlName="startingDate"
              class="form-control"
            />
          </div>

          <div *ngIf="error" class="alert alert-error">
            {{ error }}
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="onCancel()">
              Annuler
            </button>
            <button type="submit" class="btn btn-primary" [disabled]="projectForm.invalid || loading">
              <span *ngIf="loading">Création...</span>
              <span *ngIf="!loading">Créer le projet</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .project-create-container {
      display: flex;
      justify-content: center;
      padding: 2rem;
    }

    .project-create-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      padding: 2rem;
      width: 100%;
      max-width: 600px;
    }

    h2 {
      color: #2c3e50;
      margin-bottom: 1.5rem;
      text-align: center;
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

    textarea.form-control {
      resize: vertical;
      min-height: 100px;
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

    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 2rem;
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

    .btn-secondary {
      background-color: #95a5a6;
      color: white;
    }

    .btn-secondary:hover {
      background-color: #7f8c8d;
    }
  `]
})
export class ProjectCreateComponent implements OnInit {
  projectForm!: FormGroup;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.projectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      startingDate: ['']
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.projectForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      this.loading = true;
      this.error = null;

      const project: Project = {
        name: this.projectForm.value.name,
        description: this.projectForm.value.description || undefined,
        startingDate: this.projectForm.value.startingDate || undefined
      };

      this.projectService.createProject(project).subscribe({
        next: (createdProject) => {
          this.router.navigate(['/projects']);
        },
        error: (err) => {
          this.loading = false;
          if (err.error && typeof err.error === 'string') {
            this.error = err.error;
          } else {
            this.error = 'Erreur lors de la création du projet. Veuillez réessayer.';
          }
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/projects']);
  }
}
