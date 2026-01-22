import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { TaskService } from './task.service';
import { Task, TaskStatus, TaskPriority } from '../models/task.model';
import { environment } from '../../environments/environment';
import { SessionService } from './session.service';
import { AuthResponse } from '../models/auth.model';
import { authInterceptor } from '../interceptors/auth.interceptor';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  const apiUrl = `${environment.apiUrl}/tasks`;
  const mockUser: AuthResponse = {
    userId: 'test-user-id',
    username: 'testuser',
    email: 'test@example.com',
    token: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskService,
        SessionService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TaskService);
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

  it('should get all tasks', () => {
    const mockTasks: Task[] = [
      { id: '1', name: 'Task 1', description: 'Description 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM },
      { id: '2', name: 'Task 2', description: 'Description 2', status: TaskStatus.IN_PROGRESS, priority: TaskPriority.HIGH }
    ];

    service.getAllTasks().subscribe(tasks => {
      expect(tasks.length).toBe(2);
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockTasks);
  });

  it('should get task by id', () => {
    const mockTask: Task = { id: '1', name: 'Task 1', description: 'Description 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM };

    service.getTaskById('1').subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockTask);
  });

  it('should create a task', () => {
    const newTask: Task = { name: 'New Task', description: 'New Description', status: TaskStatus.TODO, priority: TaskPriority.LOW };
    const createdTask: Task = { id: '1', ...newTask };

    service.createTask(newTask).subscribe(task => {
      expect(task).toEqual(createdTask);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(newTask);
    req.flush(createdTask);
  });

  it('should update a task', () => {
    const updatedTask: Task = { id: '1', name: 'Updated Task', description: 'Updated Description', status: TaskStatus.IN_PROGRESS, priority: TaskPriority.HIGH };

    service.updateTask('1', updatedTask).subscribe(task => {
      expect(task).toEqual(updatedTask);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(updatedTask);
    req.flush(updatedTask);
  });

  it('should delete a task', () => {
    service.deleteTask('1').subscribe(() => {
      expect(true).toBeTruthy();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(null);
  });
});

