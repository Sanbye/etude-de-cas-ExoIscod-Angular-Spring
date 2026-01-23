export interface Task {
  id?: string;
  name: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: string;
  endDate?: string;
  projectId?: string;
  assignedUserId?: string;
  projectMembers?: ProjectMember[];
}

export interface ProjectMember {
  projectId: string;
  userId: string;
  userEmail?: string;
  userName?: string;
  role: Role;
}

export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export enum Role {
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
  OBSERVER = 'OBSERVER'
}

export interface TaskHistory {
  id?: string;
  taskId?: string;
  fieldName: string;
  oldValue?: string;
  newValue?: string;
  modifiedAt?: string;
  modifiedBy?: string;
}

export interface AssignTaskResponse {
  task: Task;
  userEmail: string;
  taskTitle: string;
  projectName: string;
  emailSent: boolean;
}