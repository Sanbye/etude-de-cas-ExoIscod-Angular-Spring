import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { Project, ProjectStatus } from '../models/project.model';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  const apiUrl = 'http://localhost:8080/api/projects';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all projects', () => {
    const mockProjects: Project[] = [
      { id: 1, name: 'Project 1', description: 'Description 1', status: ProjectStatus.IN_PROGRESS },
      { id: 2, name: 'Project 2', description: 'Description 2', status: ProjectStatus.PLANNED }
    ];

    service.getAllProjects().subscribe(projects => {
      expect(projects.length).toBe(2);
      expect(projects).toEqual(mockProjects);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockProjects);
  });

  it('should get project by id', () => {
    const mockProject: Project = { id: 1, name: 'Project 1', description: 'Description 1', status: ProjectStatus.IN_PROGRESS };

    service.getProjectById('1').subscribe(project => {
      expect(project).toEqual(mockProject);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProject);
  });

  it('should create a project', () => {
    const newProject: Project = { name: 'New Project', description: 'New Description', status: ProjectStatus.PLANNED };
    const createdProject: Project = { id: 1, ...newProject };

    service.createProject(newProject).subscribe(project => {
      expect(project).toEqual(createdProject);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newProject);
    req.flush(createdProject);
  });

  it('should update a project', () => {
    const updatedProject: Project = { id: 1, name: 'Updated Project', description: 'Updated Description', status: ProjectStatus.IN_PROGRESS };

    service.updateProject('1', updatedProject).subscribe(project => {
      expect(project).toEqual(updatedProject);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedProject);
    req.flush(updatedProject);
  });

  it('should delete a project', () => {
    service.deleteProject('1').subscribe(() => {
      expect(true).toBeTruthy();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

