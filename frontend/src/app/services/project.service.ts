import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  getProjectById(id: string): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  updateProject(id: string, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project);
  }

  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  inviteMember(projectId: string, request: { email: string; role: string }): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/${projectId}/invite`, request);
  }

  getProjectMembers(projectId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${projectId}/members`);
  }

  updateMemberRole(projectId: string, userId: string, role: string): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/members/${userId}/role`, { role });
  }
}

