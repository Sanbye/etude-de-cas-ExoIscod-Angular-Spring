import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="user-list-container">
      <h2>Liste des Utilisateurs</h2>
      <div *ngIf="users.length === 0" class="loading">Chargement...</div>
      <table *ngIf="users.length > 0" class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nom d'utilisateur</th>
            <th>Email</th>
            <th>Prénom</th>
            <th>Nom</th>
            <th>Date de création</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of users">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.firstName }}</td>
            <td>{{ user.lastName }}</td>
            <td>{{ user.createdAt | date:'short' }}</td>
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
  `]
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.http.get<User[]>(this.apiUrl).subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Erreur lors du chargement des utilisateurs:', err)
    });
  }
}

