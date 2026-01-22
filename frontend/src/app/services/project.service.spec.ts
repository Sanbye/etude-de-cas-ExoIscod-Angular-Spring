import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ProjectService } from './project.service';
import { Project } from '../models/project.model';
import { environment } from '../../environments/environment';
import { SessionService } from './session.service';
import { AuthResponse } from '../models/auth.model';
import { authInterceptor } from '../interceptors/auth.interceptor';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  const apiUrl = `${environment.apiUrl}/projects`;
  const mockUser: AuthResponse = {
    userId: 'test-user-id',
    username: 'testuser',
    email: 'test@example.com',
    token: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProjectService,
        SessionService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
    // Mock un utilisateur connecté
    sessionService.setCurrentUser(mockUser);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all projects', () => {
    const mockProjects: Project[] = [
      { id: '1', name: 'Project 1', description: 'Description 1', startingDate: '2026-01-01' },
      { id: '2', name: 'Project 2', description: 'Description 2', startingDate: '2026-01-02' }
    ];

    service.getAllProjects().subscribe(projects => {
      expect(projects.length).toBe(2);
      expect(projects).toEqual(mockProjects);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockProjects);
  });

  it('should get project by id', () => {
    const mockProject: Project = { id: '1', name: 'Project 1', description: 'Description 1', startingDate: '2026-01-01' };

    service.getProjectById('1').subscribe(project => {
      expect(project).toEqual(mockProject);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockProject);
  });

  it('should create a project', () => {
    const newProject: Project = { name: 'New Project', description: 'New Description' };
    const createdProject: Project = { id: '1', ...newProject };

    service.createProject(newProject).subscribe(project => {
      expect(project).toEqual(createdProject);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(newProject);
    req.flush(createdProject);
  });

  it('should update a project', () => {
    const updatedProject: Project = { id: '1', name: 'Updated Project', description: 'Updated Description' };

    service.updateProject('1', updatedProject).subscribe(project => {
      expect(project).toEqual(updatedProject);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(updatedProject);
    req.flush(updatedProject);
  });

  it('should delete a project', () => {
    service.deleteProject('1').subscribe(() => {
      expect(true).toBeTruthy();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(null);
  });
});

