import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  getTaskById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(projectId: string, task: { name: string; description?: string; dueDate?: string; priority: string }): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, {
      projectId: projectId,
      name: task.name,
      description: task.description,
      dueDate: task.dueDate,
      priority: task.priority
    });
  }

  getTasksByProject(projectId: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/project/${projectId}`);
  }

  updateTask(id: string, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  assignTask(taskId: string, projectId: string, userId: string): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/${taskId}/assign`, {
      projectId: projectId,
      userId: userId
    });
  }
}

