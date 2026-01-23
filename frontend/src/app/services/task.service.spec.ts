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

  it('should create a task', () => {
    const taskData = {
      name: 'Test Task',
      description: 'Test Description',
      dueDate: '2024-12-31',
      priority: TaskPriority.HIGH
    };
    const projectId = 'test-project-id';
    const mockTask: Task = { 
      id: '1', 
      name: taskData.name,
      description: taskData.description,
      dueDate: taskData.dueDate,
      priority: taskData.priority,
      status: TaskStatus.TODO 
    };

    service.createTask(projectId, taskData).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/tasks`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      projectId: projectId,
      name: taskData.name,
      description: taskData.description,
      dueDate: taskData.dueDate,
      priority: taskData.priority
    });
    req.flush(mockTask);
  });

  it('should get tasks by project', () => {
    const projectId = 'test-project-id';
    const mockTasks: Task[] = [
      { id: '1', name: 'Task 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM },
      { id: '2', name: 'Task 2', status: TaskStatus.IN_PROGRESS, priority: TaskPriority.HIGH }
    ];

    service.getTasksByProject(projectId).subscribe(tasks => {
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/tasks/project/${projectId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTasks);
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
    const projectId = 'test-project-id';
    const taskData = {
      name: 'New Task',
      description: 'New Description',
      dueDate: '2024-12-31',
      priority: TaskPriority.HIGH
    };
    const createdTask: Task = { 
      id: '1', 
      name: taskData.name,
      description: taskData.description,
      dueDate: taskData.dueDate,
      priority: taskData.priority,
      status: TaskStatus.TODO 
    };

    service.createTask(projectId, taskData).subscribe(task => {
      expect(task).toEqual(createdTask);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual({
      projectId: projectId,
      name: taskData.name,
      description: taskData.description,
      dueDate: taskData.dueDate,
      priority: taskData.priority
    });
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

  it('should assign a task to a project member', () => {
    const taskId = '1';
    const projectId = 'project-1';
    const userId = 'user-1';
    const assignedTask: Task = { 
      id: taskId, 
      name: 'Assigned Task', 
      status: TaskStatus.TODO, 
      priority: TaskPriority.MEDIUM,
      assignedUserId: userId,
      projectId: projectId
    };

    service.assignTask(taskId, projectId, userId).subscribe(task => {
      expect(task).toEqual(assignedTask);
      expect(task.assignedUserId).toBe(userId);
    });

    const req = httpMock.expectOne(`${apiUrl}/${taskId}/assign`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual({
      projectId: projectId,
      userId: userId
    });
    req.flush(assignedTask);
  });
});

