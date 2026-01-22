import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { UserService } from './user.service';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';
import { SessionService } from './session.service';
import { AuthResponse } from '../models/auth.model';
import { authInterceptor } from '../interceptors/auth.interceptor';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  const apiUrl = `${environment.apiUrl}/users`;
  const mockUser: AuthResponse = {
    userId: 'test-user-id',
    username: 'testuser',
    email: 'test@example.com',
    token: null
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        SessionService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UserService);
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

  it('should get all users', () => {
    const mockUsers: User[] = [
      { id: '1', userName: 'user1', email: 'user1@example.com' },
      { id: '2', userName: 'user2', email: 'user2@example.com' }
    ];

    service.getAllUsers().subscribe(users => {
      expect(users.length).toBe(2);
      expect(users).toEqual(mockUsers);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockUsers);
  });

  it('should get user by id', () => {
    const mockUserData: User = { id: '1', userName: 'user1', email: 'user1@example.com' };

    service.getUserById('1').subscribe(user => {
      expect(user).toEqual(mockUserData);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(mockUserData);
  });

  it('should create a user', () => {
    const newUser: User = { userName: 'newuser', email: 'newuser@example.com' };
    const createdUser: User = { id: '1', ...newUser };

    service.createUser(newUser).subscribe(user => {
      expect(user).toEqual(createdUser);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(newUser);
    req.flush(createdUser);
  });

  it('should update a user', () => {
    const updatedUser: User = { id: '1', userName: 'user1', email: 'updated@example.com' };

    service.updateUser('1', updatedUser).subscribe(user => {
      expect(user).toEqual(updatedUser);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    expect(req.request.body).toEqual(updatedUser);
    req.flush(updatedUser);
  });

  it('should delete a user', () => {
    service.deleteUser('1').subscribe(() => {
      expect(true).toBeTruthy();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('X-User-Id')).toBe(mockUser.userId);
    req.flush(null);
  });
});

