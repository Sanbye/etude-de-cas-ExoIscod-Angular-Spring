import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectListComponent } from './project-list.component';
import { ProjectService } from '../../services/project.service';
import { of, throwError, Subject } from 'rxjs';
import { Project } from '../../models/project.model';
import { Router, provideRouter } from '@angular/router';
import { SessionService } from '../../services/session.service';

describe('ProjectListComponent', () => {
  let component: ProjectListComponent;
  let fixture: ComponentFixture<ProjectListComponent>;
  let projectService: jasmine.SpyObj<ProjectService>;
  let sessionServiceMock: Pick<SessionService, 'getCurrentUser'>;

  const mockProjects: Project[] = [
    { id: '1', name: 'Project 1', description: 'Description 1', startingDate: '2026-01-01' },
    { id: '2', name: 'Project 2', description: 'Description 2', startingDate: '2026-01-02' }
  ];

  beforeEach(async () => {
    const projectServiceSpy = jasmine.createSpyObj('ProjectService', [
      'getAllProjects',
      'getProjectsByMember',
      'getProjectMembers'
    ]);

    sessionServiceMock = {
      getCurrentUser: () => null
    };

    projectServiceSpy.getProjectsByMember.and.returnValue(of([]));
    projectServiceSpy.getProjectMembers.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ProjectListComponent],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: SessionService, useValue: sessionServiceMock },
        provideRouter([])
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

  it('should load my projects and roles when user is logged in', () => {
    const userId = 'user-1';
    sessionServiceMock.getCurrentUser = () => ({ userId } as any);

    projectService.getAllProjects.and.returnValue(of(mockProjects));
    projectService.getProjectsByMember.and.returnValue(of([mockProjects[0]]));
    projectService.getProjectMembers.and.returnValue(of([
      { userId, role: 'ADMIN' }
    ]));

    fixture.detectChanges();

    expect(component.myProjects.length).toBe(1);
    expect(component.otherProjects.length).toBe(1);
    expect(component.getRoleLabel(mockProjects[0].id!)).toBe('ADMIN');
    expect(component.getRoleBadgeClass(mockProjects[0].id!)).toBe('badge--admin');
  });

  it('should handle error when loading my projects', () => {
    const userId = 'user-1';
    sessionServiceMock.getCurrentUser = () => ({ userId } as any);

    projectService.getAllProjects.and.returnValue(of(mockProjects));
    projectService.getProjectsByMember.and.returnValue(throwError(() => new Error('API error')));

    fixture.detectChanges();

    expect(component.myProjects.length).toBe(0);
    expect(component.otherProjects.length).toBe(mockProjects.length);
    expect(component.getRoleLabel(mockProjects[0].id!)).toBe('Rôle inconnu');
    expect(component.getRoleBadgeClass(mockProjects[0].id!)).toBe('badge--unknown');
  });

  it('should handle missing role values gracefully', () => {
    component.myProjectRoles = {};
    expect(component.getRoleLabel('missing')).toBe('Rôle inconnu');
    expect(component.getRoleBadgeClass('missing')).toBe('badge--unknown');
  });

  it('should navigate to project details', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    component.viewProject('project-123');
    expect(router.navigate).toHaveBeenCalledWith(['/projects', 'project-123']);
  });

  it('should handle loadMyProjectRoles when no valid project ids', () => {
    component.myProjects = [{ name: 'No Id Project' } as Project];
    (component as any).loadMyProjectRoles('user-1');
    expect(component.myProjectRoles).toEqual({});
  });

  it('should return viewer badge class when role is VIEWER', () => {
    component.myProjectRoles = { 'p1': 'VIEWER' };
    expect(component.getRoleBadgeClass('p1')).toBe('badge--viewer');
  });
});

