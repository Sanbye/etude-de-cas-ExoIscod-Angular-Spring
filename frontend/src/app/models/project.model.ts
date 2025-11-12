import { User } from './user.model';

export interface Project {
  id?: number;
  name: string;
  description?: string;
  status: ProjectStatus;
  owner?: User;
  ownerId?: number;
  members?: User[];
  createdAt?: string;
  updatedAt?: string;
}

export enum ProjectStatus {
  PLANNED = 'PLANNED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

