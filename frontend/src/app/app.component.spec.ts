import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { SessionService } from './services/session.service';
import { AuthService } from './services/auth.service';
import { BehaviorSubject } from 'rxjs';
import { AuthResponse } from './models/auth.model';

describe('AppComponent', () => {
  let sessionService: jasmine.SpyObj<SessionService>;
  let authService: jasmine.SpyObj<AuthService>;
  let currentUserSubject: BehaviorSubject<AuthResponse | null>;

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    const sessionServiceSpy = jasmine.createSpyObj('SessionService', ['getCurrentUser', 'clearCurrentUser'], {
      currentUser$: currentUserSubject.asObservable()
    });
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'logout']);

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: SessionService, useValue: sessionServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    sessionService = TestBed.inject(SessionService) as jasmine.SpyObj<SessionService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should have title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toBe('project-management-frontend');
  });

  it('should render navigation links when authenticated', () => {
    const mockUser: AuthResponse = {
      userId: '1',
      username: 'testuser',
      email: 'test@example.com',
      token: null
    };
    sessionService.getCurrentUser.and.returnValue(mockUser);
    authService.isAuthenticated.and.returnValue(true);
    currentUserSubject.next(mockUser);

    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const navLinks = compiled.querySelectorAll('nav a');
    expect(navLinks.length).toBeGreaterThanOrEqual(3); // Au moins 3 liens (Users, Projects, Tasks)
  });
});

