import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class PatientGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    // First check if user is logged in
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    // Only patients (normal users) can access patient pages
    if (currentUser.role === 'ADMIN' || currentUser.role === 'SUPERADMIN') {
      this.router.navigate(['/admin/dashboard']);
      return false;
    }

    if (currentUser.role === 'DOCTOR') {
      this.router.navigate(['/doctor/dashboard']);
      return false;
    }

    return true;
  }
}
