import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { SessionService } from './services/session.service';
import { AuthService } from './services/auth.service';
import { AuthResponse } from './models/auth.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule],
  template: `
    <div class="app-container">
      <header *ngIf="isAuthenticated">
        <h1>Gestion de Projets</h1>
        <nav>
          <a routerLink="/users">Utilisateurs</a>
          <a routerLink="/projects">Projets</a>
          <a routerLink="/tasks">Tâches</a>
          <div class="user-info">
            <span *ngIf="currentUser">{{ currentUser.username }}</span>
            <button (click)="logout()" class="btn-logout">Déconnexion</button>
          </div>
        </nav>
      </header>
      <main>
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    header {
      background-color: #2c3e50;
      color: white;
      padding: 1rem 2rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    header h1 {
      margin: 0 0 1rem 0;
      font-size: 1.5rem;
    }
    nav {
      display: flex;
      gap: 1rem;
      align-items: center;
      justify-content: space-between;
    }
    nav a {
      color: white;
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: background-color 0.3s;
    }
    nav a:hover {
      background-color: #34495e;
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-left: auto;
    }
    .user-info span {
      color: white;
    }
    .btn-logout {
      background-color: #e74c3c;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s;
    }
    .btn-logout:hover {
      background-color: #c0392b;
    }
    main {
      flex: 1;
      padding: 2rem;
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'project-management-frontend';
  isAuthenticated = false;
  currentUser: AuthResponse | null = null;
  private subscription?: Subscription;

  constructor(
    private sessionService: SessionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscription = this.sessionService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isAuthenticated = this.authService.isAuthenticated();
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

