import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { ToastComponent } from './shared/components/toast/toast.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastComponent, NavbarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'hospital-management-frontend';
  currentUrl = '';

  constructor(private router: Router, private authService: AuthService) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentUrl = event.url;
      });
  }

  isAuthPage(): boolean {
    return this.currentUrl === '/auth/login' || this.currentUrl === '/auth/register';
  }

  shouldShowNavbar(): boolean {
    // Don't show navbar on auth pages
    if (this.isAuthPage()) {
      return false;
    }

    // Don't show navbar if user is not logged in
    if (!this.authService.isLoggedIn()) {
      return false;
    }

    const currentUser = this.authService.getCurrentUser();
    
    // Don't show navbar for admin or doctor pages
    if (currentUser) {
      if (currentUser.role === 'ADMIN' || currentUser.role === 'SUPERADMIN') {
        return false; // Admin has their own layout
      }
      if (currentUser.role === 'DOCTOR') {
        return false; // Doctor has their own layout
      }
    }

    // Show navbar only for patient (normal user) pages
    return true;
  }
}
