import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserListComponent } from './user-list.component';
import { UserService } from '../../services/user.service';
import { of, throwError, Subject } from 'rxjs';
import { User } from '../../models/user.model';

describe('UserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;
  let userService: jasmine.SpyObj<UserService>;

  const mockUsers: User[] = [
    { id: '1', userName: 'user1', email: 'user1@example.com' },
    { id: '2', userName: 'user2', email: 'user2@example.com' }
  ];

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['getAllUsers']);

    await TestBed.configureTestingModule({
      imports: [UserListComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserListComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on init', () => {
    userService.getAllUsers.and.returnValue(of(mockUsers));

    fixture.detectChanges();

    expect(component.users).toEqual(mockUsers);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(userService.getAllUsers).toHaveBeenCalled();
  });

  it('should handle error when loading users', () => {
    const errorMessage = 'Error loading users';
    userService.getAllUsers.and.returnValue(throwError(() => ({ message: errorMessage })));

    fixture.detectChanges();

    expect(component.users).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBe('Impossible de charger les utilisateurs. Vérifiez que le backend est démarré.');
  });

  it('should display loading state', () => {
    const usersSubject = new Subject<User[]>();
    userService.getAllUsers.and.returnValue(usersSubject.asObservable());
    
    fixture.detectChanges();
    expect(component.loading).toBe(true);
    const compiled = fixture.nativeElement as HTMLElement;
    const loadingElement = compiled.querySelector('.loading');
    expect(loadingElement).toBeTruthy();
    
    usersSubject.next(mockUsers);
    usersSubject.complete();
  });

  it('should display error message', () => {

    userService.getAllUsers.and.returnValue(of(mockUsers));
    
    fixture.detectChanges();
    component.error = 'Test error';
    component.loading = false;

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error');
    expect(errorElement).toBeTruthy();
    expect(errorElement?.textContent).toContain('Test error');
  });

  it('should display users table when users are loaded', () => {
    userService.getAllUsers.and.returnValue(of(mockUsers));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const table = compiled.querySelector('.user-table');
    expect(table).toBeTruthy();
  });

  it('should display empty message when no users', () => {
    userService.getAllUsers.and.returnValue(of([]));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const emptyElement = compiled.querySelector('.empty');
    expect(emptyElement).toBeTruthy();
    expect(emptyElement?.textContent).toContain('Aucun utilisateur trouvé');
  });
});

