import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectListComponent } from './project-list.component';
import { ProjectService } from '../../services/project.service';
import { of, throwError, Subject } from 'rxjs';
import { Project, ProjectStatus } from '../../models/project.model';

describe('ProjectListComponent', () => {
  let component: ProjectListComponent;
  let fixture: ComponentFixture<ProjectListComponent>;
  let projectService: jasmine.SpyObj<ProjectService>;

  const mockProjects: Project[] = [
    { id: 1, name: 'Project 1', description: 'Description 1', status: ProjectStatus.IN_PROGRESS },
    { id: 2, name: 'Project 2', description: 'Description 2', status: ProjectStatus.PLANNED }
  ];

  beforeEach(async () => {
    const projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects']);

    await TestBed.configureTestingModule({
      imports: [ProjectListComponent],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectListComponent);
    component = fixture.componentInstance;
    projectService = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));

    fixture.detectChanges();

    expect(component.projects).toEqual(mockProjects);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(projectService.getAllProjects).toHaveBeenCalled();
  });

  it('should handle error when loading projects', () => {
    const errorMessage = 'Error loading projects';
    projectService.getAllProjects.and.returnValue(throwError(() => ({ message: errorMessage })));

    fixture.detectChanges();

    expect(component.projects).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBe('Impossible de charger les projets. Vérifiez que le backend est démarré.');
  });

  it('should display loading state', () => {
    const projectsSubject = new Subject<Project[]>();
    projectService.getAllProjects.and.returnValue(projectsSubject.asObservable());
    
    fixture.detectChanges();
    expect(component.loading).toBe(true);
    const compiled = fixture.nativeElement as HTMLElement;
    const loadingElement = compiled.querySelector('.loading');
    expect(loadingElement).toBeTruthy();
    
    projectsSubject.next(mockProjects);
    projectsSubject.complete();
  });

  it('should display error message', () => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    
    fixture.detectChanges();
    component.error = 'Test error';
    component.loading = false;

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error');
    expect(errorElement).toBeTruthy();
    expect(errorElement?.textContent).toContain('Test error');
  });

  it('should display projects grid when projects are loaded', () => {
    projectService.getAllProjects.and.returnValue(of(mockProjects));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const grid = compiled.querySelector('.projects-grid');
    expect(grid).toBeTruthy();
  });

  it('should display empty message when no projects', () => {
    projectService.getAllProjects.and.returnValue(of([]));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const emptyElement = compiled.querySelector('.empty');
    expect(emptyElement).toBeTruthy();
    expect(emptyElement?.textContent).toContain('Aucun projet trouvé');
  });
});

