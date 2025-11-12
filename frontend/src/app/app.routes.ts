import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/projects',
    pathMatch: 'full'
  },
  {
    path: 'users',
    loadComponent: () => import('./users/user-list/user-list.component').then(m => m.UserListComponent)
  },
  {
    path: 'projects',
    loadComponent: () => import('./projects/project-list/project-list.component').then(m => m.ProjectListComponent)
  },
  {
    path: 'tasks',
    loadComponent: () => import('./tasks/task-list/task-list.component').then(m => m.TaskListComponent)
  }
];

