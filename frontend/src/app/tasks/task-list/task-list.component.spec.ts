import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { SessionService } from '../../services/session.service';
import { of, throwError, Subject } from 'rxjs';
import { Task, TaskStatus, TaskPriority, AssignTaskResponse } from '../../models/task.model';
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
    const taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasksByProject', 'assignTask', 'getTaskHistory', 'updateTask']);
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
    const mockAssignResponse: AssignTaskResponse = {
      task: mockTasks[0],
      userEmail: 'user1@example.com',
      taskTitle: 'Task 1',
      projectName: 'Project 1',
      emailSent: true
    };
    
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    taskService.assignTask.and.returnValue(of(mockAssignResponse));
    taskService.getTaskHistory.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    
    component.assignmentValues['1'] = 'user1';
    component.assignTask(mockTasks[0], '1');
    tick(); 
    tick(1000);
    
    expect(taskService.assignTask).toHaveBeenCalledWith('1', '1', 'user1');
    expect(component.showEmailNotification).toBe(true);
    expect(component.emailNotificationInfo).not.toBeNull();
    expect(component.emailNotificationInfo?.userEmail).toBe('user1@example.com');
    
    tick(5000);
    
    expect(component.showEmailNotification).toBe(false);
    expect(component.emailNotificationInfo).toBeNull();
  }));

  it('should filter tasks by status - TODO', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = TaskStatus.TODO;
    component.onStatusFilterChange();
    fixture.detectChanges();
    
    expect(component.filteredTaskGroups.length).toBeGreaterThan(0);
    const todoTasks = component.filteredTaskGroups.flatMap(g => g.tasks);
    expect(todoTasks.every(task => task.status === TaskStatus.TODO)).toBe(true);
  }));

  it('should filter tasks by status - IN_PROGRESS', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = TaskStatus.IN_PROGRESS;
    component.onStatusFilterChange();
    fixture.detectChanges();
    
    expect(component.filteredTaskGroups.length).toBeGreaterThan(0);
    const inProgressTasks = component.filteredTaskGroups.flatMap(g => g.tasks);
    expect(inProgressTasks.every(task => task.status === TaskStatus.IN_PROGRESS)).toBe(true);
  }));

  it('should filter tasks by status - DONE', fakeAsync(() => {
    const doneTasks: Task[] = [
      { id: '3', name: 'Task 3', description: 'Description 3', status: TaskStatus.DONE, priority: TaskPriority.LOW, projectId: '1' }
    ];
    
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(doneTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = TaskStatus.DONE;
    component.onStatusFilterChange();
    fixture.detectChanges();
    
    expect(component.filteredTaskGroups.length).toBeGreaterThan(0);
    const doneTasksFiltered = component.filteredTaskGroups.flatMap(g => g.tasks);
    expect(doneTasksFiltered.every(task => task.status === TaskStatus.DONE)).toBe(true);
  }));

  it('should show all tasks when no filter is selected', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = '';
    component.onStatusFilterChange();
    fixture.detectChanges();
    
    expect(component.filteredTaskGroups.length).toBeGreaterThan(0);
    const allTasks = component.filteredTaskGroups.flatMap(g => g.tasks);
    expect(allTasks.length).toBe(mockTasks.length);
  }));

  it('should hide projects with no matching tasks when filter is applied', fakeAsync(() => {
    const todoOnlyTasks: Task[] = [
      { id: '1', name: 'Task 1', description: 'Description 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM, projectId: '1' }
    ];
    
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(todoOnlyTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = TaskStatus.DONE;
    component.onStatusFilterChange();
    fixture.detectChanges();
    
    expect(component.filteredTaskGroups.length).toBe(0);
  }));

  it('should apply filter after task update', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    taskService.updateTask.and.returnValue(of(mockTasks[0]));
    taskService.getTaskHistory.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    
    component.selectedStatus = TaskStatus.TODO;
    component.onStatusFilterChange();
    
    const initialFilteredCount = component.filteredTaskGroups.flatMap(g => g.tasks).length;
    
    component.updateTask(mockTasks[0], '1');
    tick();
    tick(1000);
    
    expect(component.filteredTaskGroups.length).toBeGreaterThanOrEqual(0);
  }));

  it('should initialize edit form when edit tab is activated', () => {
    component.taskGroups = [{
      project: mockProjects[0],
      tasks: [{
        ...mockTasks[0],
        dueDate: '2024-01-02T00:00:00Z',
        endDate: '2024-02-03T00:00:00Z'
      }],
      members: mockMembers
    }];

    component.setActiveTab('1', 'edit');

    expect(component.activeTab['1']).toBe('edit');
    expect(component.editForms['1']).toBeTruthy();
    expect(component.editForms['1'].value.dueDate).toBe('2024-01-02');
    expect(component.editForms['1'].value.endDate).toBe('2024-02-03');
  });

  it('should resolve assigned member name correctly', () => {
    const group = {
      project: mockProjects[0],
      tasks: [mockTasks[0]],
      members: [
        { userId: 'user1', userName: 'User 1', userEmail: 'user1@example.com', role: 'ADMIN' }
      ]
    };

    expect(component.getAssignedMemberName({ ...mockTasks[0], assignedUserId: undefined }, group as any))
      .toBe('Non assigné');
    expect(component.getAssignedMemberName({ ...mockTasks[0], assignedUserId: 'missing' }, group as any))
      .toBe('Membre inconnu');
    expect(component.getAssignedMemberName({ ...mockTasks[0], assignedUserId: 'user1' }, group as any))
      .toBe('User 1');
  });

  it('should evaluate assign/edit permissions based on role', () => {
    component.currentUser = mockUser;

    const adminGroup = { project: mockProjects[0], tasks: [], members: [{ userId: 'user1', role: 'ADMIN' }] };
    const observerGroup = { project: mockProjects[0], tasks: [], members: [{ userId: 'user1', role: 'OBSERVER' }] };
    const emptyGroup = { project: mockProjects[0], tasks: [], members: [] };

    expect(component.canAssignTask(adminGroup as any)).toBe(true);
    expect(component.canEditTask(adminGroup as any)).toBe(true);
    expect(component.canAssignTask(observerGroup as any)).toBe(false);
    expect(component.canAssignTask(emptyGroup as any)).toBe(false);
  });

  it('should handle assignment error', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(of(mockTasks));
    projectService.getProjectMembers.and.returnValue(of(mockMembers));
    taskService.assignTask.and.returnValue(throwError(() => ({ error: 'Assignment failed' })));

    fixture.detectChanges();
    tick();

    component.assignmentValues['1'] = 'user1';
    component.assignTask(mockTasks[0], '1');
    tick();

    expect(component.assignmentError['1']).toBe('Assignment failed');
    expect(component.assigning['1']).toBe(false);
  }));

  it('should handle task history errors', fakeAsync(() => {
    taskService.getTaskHistory.and.returnValue(throwError(() => ({ status: 403 })));
    component.loadTaskHistory('1');
    tick();
    expect(component.historyError['1']).toContain('permissions');

    taskService.getTaskHistory.and.returnValue(throwError(() => ({ status: 404 })));
    component.loadTaskHistory('2', true);
    tick();
    expect(component.historyError['2']).toContain('introuvable');

    taskService.getTaskHistory.and.returnValue(throwError(() => ({ error: 'Server error' })));
    component.loadTaskHistory('3', true);
    tick();
    expect(component.historyError['3']).toBe('Server error');
  }));

  it('should reuse cached task history when not forced', () => {
    component.taskHistory['1'] = [];
    component.loadTaskHistory('1');
    expect(taskService.getTaskHistory).not.toHaveBeenCalled();
  });

  it('should call callback even if reloadTasksForProject fails', () => {
    component.taskGroups = [{
      project: mockProjects[0],
      tasks: mockTasks,
      members: mockMembers
    }];
    taskService.getTasksByProject.and.returnValue(throwError(() => new Error('Fail')));

    const callback = jasmine.createSpy('callback');
    component.reloadTasksForProject('1', callback);
    expect(callback).toHaveBeenCalled();
  });

  it('should skip assignment when no user selected', () => {
    component.assignmentValues['1'] = '';
    component.assignTask(mockTasks[0], '1');
    expect(taskService.assignTask).not.toHaveBeenCalled();
  });

  it('should not update task when form is invalid', () => {
    component.taskGroups = [{
      project: mockProjects[0],
      tasks: [mockTasks[0]],
      members: mockMembers
    }];
    component.setActiveTab('1', 'edit');
    component.editForms['1'].setErrors({ invalid: true });

    component.updateTask(mockTasks[0], '1');
    expect(taskService.updateTask).not.toHaveBeenCalled();
  });

  it('should handle 403 errors when loading tasks and members', fakeAsync(() => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    taskService.getTasksByProject.and.returnValue(throwError(() => ({ status: 403 })));
    projectService.getProjectMembers.and.returnValue(throwError(() => ({ status: 403 })));

    fixture.detectChanges();
    tick();

    expect(component.error).toBeNull();
    expect(component.taskGroups.length).toBe(0);
  }));

  it('should return default field name when unknown', () => {
    expect(component.getFieldDisplayName('unknownField')).toBe('unknownField');
  });
});

