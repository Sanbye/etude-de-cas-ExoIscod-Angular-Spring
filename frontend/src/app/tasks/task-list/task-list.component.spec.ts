import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { SessionService } from '../../services/session.service';
import { of, throwError, Subject } from 'rxjs';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';
import { Project } from '../../models/project.model';
import { AuthResponse } from '../../models/auth.model';
import { provideRouter } from '@angular/router';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;
  let projectService: jasmine.SpyObj<ProjectService>;
  let sessionService: SessionService;

  const mockProjects: Project[] = [
    { id: '1', name: 'Project 1', description: 'Description 1', startingDate: '2024-01-01' }
  ];

  const mockTasks: Task[] = [
    { id: '1', name: 'Task 1', description: 'Description 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM, projectId: '1' },
    { id: '2', name: 'Task 2', description: 'Description 2', status: TaskStatus.IN_PROGRESS, priority: TaskPriority.HIGH, projectId: '1' }
  ];

  const mockMembers: any[] = [
    { userId: 'user1', userName: 'User 1', userEmail: 'user1@example.com', role: 'ADMIN' }
  ];

  const mockUser: AuthResponse = {
    userId: 'user1',
    username: 'testuser',
    email: 'test@example.com',
    token: null
  };

  beforeEach(async () => {
    const taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasksByProject', 'assignTask']);
    const projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects', 'getProjectMembers']);

    await TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: ProjectService, useValue: projectServiceSpy },
        SessionService,
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    projectService = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    sessionService = TestBed.inject(SessionService);
    sessionService.setCurrentUser(mockUser);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks grouped by project on init', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));

    fixture.detectChanges();
    tick();

    expect(component.taskGroups.length).toBeGreaterThan(0);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(projectService.getAllProjects).toHaveBeenCalled();
  }));

  it('should handle error when loading projects', () => {
    const errorMessage = 'Error loading projects';
    projectService.getAllProjects.and.returnValue(throwError(() => ({ message: errorMessage })));

    fixture.detectChanges();

    expect(component.taskGroups).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBe('Impossible de charger les projets. Vérifiez que le backend est démarré.');
  });

  it('should display loading state', fakeAsync(() => {
    const projectsSubject = new Subject<Project[]>();
    projectService.getAllProjects.and.returnValue(projectsSubject.asObservable());
    // Mocker les appels pour chaque projet
    taskService.getTasksByProject.and.returnValue(of([]));
    projectService.getProjectMembers.and.returnValue(of([]));
    
    fixture.detectChanges();
    expect(component.loading).toBe(true);
    const compiled = fixture.nativeElement as HTMLElement;
    const loadingElement = compiled.querySelector('.loading');
    expect(loadingElement).toBeTruthy();
    
    projectsSubject.next(mockProjects);
    projectsSubject.complete();
    tick();
    fixture.detectChanges();
  }));

  it('should display error message', () => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    component.error = 'Test error';
    component.loading = false;

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error');
    expect(errorElement).toBeTruthy();
    expect(errorElement?.textContent).toContain('Test error');
  });

  it('should display tasks grouped by project when loaded', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const grid = compiled.querySelector('.tasks-grid');
    expect(grid).toBeTruthy();
  }));

  it('should display empty message when no tasks', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of([]));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const emptyElement = compiled.querySelector('.empty');
    expect(emptyElement).toBeTruthy();
    expect(emptyElement?.textContent).toContain('Aucune tâche trouvée');
  }));

  it('should assign a task to a member', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    taskService.assignTask.and.returnValue(of(mockTasks[0]));
    
    fixture.detectChanges();
    tick();
    
    component.assignmentValues['1'] = 'user1';
    component.assignTask(mockTasks[0], '1');
    tick();
    
    expect(taskService.assignTask).toHaveBeenCalledWith('1', '1', 'user1');
  }));
});

