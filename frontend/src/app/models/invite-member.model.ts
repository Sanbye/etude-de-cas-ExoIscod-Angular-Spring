import { Role } from './task.model';

export interface InviteMemberRequest {
  email: string;
  role: Role;
}
