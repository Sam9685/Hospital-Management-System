// frontend/hospital-management-frontend/src/app/core/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is logged in and has valid token
  if (authService.isLoggedIn() && authService.getCurrentUser()) {
    return true;
  }

  // If no valid session, redirect to login
  router.navigate(['/login']);
  return false;
};
