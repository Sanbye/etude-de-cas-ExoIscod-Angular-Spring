import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="user-list-container">
      <h2>Liste des Utilisateurs</h2>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="!loading && !error && users.length === 0" class="empty">Aucun utilisateur trouvé.</div>
      <table *ngIf="!loading && !error && users.length > 0" class="user-table">
        <thead>
          <tr>
            <th>Nom d'utilisateur</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of users">
            <td>{{ user.userName }}</td>
            <td>{{ user.email }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .user-list-container {
      max-width: 1200px;
      margin: 0 auto;
    }
    h2 {
      color: #2c3e50;
      margin-bottom: 1.5rem;
    }
    .user-table {
      width: 100%;
      border-collapse: collapse;
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      border-radius: 8px;
      overflow: hidden;
    }
    .user-table th {
      background-color: #34495e;
      color: white;
      padding: 1rem;
      text-align: left;
      font-weight: 600;
    }
    .user-table td {
      padding: 0.75rem 1rem;
      border-bottom: 1px solid #ecf0f1;
    }
    .user-table tr:hover {
      background-color: #f8f9fa;
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
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = true;
  error: string | null = null;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = null;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des utilisateurs:', err);
        this.error = 'Impossible de charger les utilisateurs. Vérifiez que le backend est démarré.';
        this.loading = false;
      }
    });
  }
}

