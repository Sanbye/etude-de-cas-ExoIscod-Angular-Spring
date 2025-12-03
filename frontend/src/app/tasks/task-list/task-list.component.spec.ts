import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { TaskService } from '../../services/task.service';
import { of, throwError } from 'rxjs';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;

  const mockTasks: Task[] = [
    { id: 1, title: 'Task 1', description: 'Description 1', status: TaskStatus.TODO, priority: TaskPriority.MEDIUM },
    { id: 2, title: 'Task 2', description: 'Description 2', status: TaskStatus.IN_PROGRESS, priority: TaskPriority.HIGH }
  ];

  beforeEach(async () => {
    const taskServiceSpy = jasmine.createSpyObj('TaskService', ['getAllTasks']);

    await TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init', () => {
    taskService.getAllTasks.and.returnValue(of(mockTasks));

    fixture.detectChanges();

    expect(component.tasks).toEqual(mockTasks);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(taskService.getAllTasks).toHaveBeenCalled();
  });

  it('should handle error when loading tasks', () => {
    const errorMessage = 'Error loading tasks';
    taskService.getAllTasks.and.returnValue(throwError(() => ({ message: errorMessage })));

    fixture.detectChanges();

    expect(component.tasks).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBe('Impossible de charger les tâches. Vérifiez que le backend est démarré.');
  });

  it('should display loading state', () => {
    taskService.getAllTasks.and.returnValue(of(mockTasks));
    component.loading = true;

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const loadingElement = compiled.querySelector('.loading');
    expect(loadingElement).toBeTruthy();
  });

  it('should display error message', () => {
    component.error = 'Test error';
    component.loading = false;

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error');
    expect(errorElement).toBeTruthy();
    expect(errorElement?.textContent).toContain('Test error');
  });

  it('should display tasks grid when tasks are loaded', () => {
    taskService.getAllTasks.and.returnValue(of(mockTasks));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const grid = compiled.querySelector('.tasks-grid');
    expect(grid).toBeTruthy();
  });

  it('should display empty message when no tasks', () => {
    taskService.getAllTasks.and.returnValue(of([]));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const emptyElement = compiled.querySelector('.empty');
    expect(emptyElement).toBeTruthy();
    expect(emptyElement?.textContent).toContain('Aucune tâche trouvée');
  });
});

