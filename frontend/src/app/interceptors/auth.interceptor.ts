import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionService } from '../services/session.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sessionService = inject(SessionService);
  const currentUser = sessionService.getCurrentUser();

  // Ne pas ajouter le header pour les routes d'authentification
  if (req.url.includes('/api/auth')) {
    return next(req);
  }

  if (currentUser && currentUser.userId) {
    const authReq = req.clone({
      setHeaders: {
        'X-User-Id': currentUser.userId
      }
    });
    return next(authReq);
  }

  return next(req);
};
