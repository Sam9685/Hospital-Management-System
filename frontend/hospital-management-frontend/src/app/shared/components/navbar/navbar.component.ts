import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar" [class.navbar-hidden]="shouldHideNavbar()">
      <div class="navbar-container">
        <!-- Logo Section -->
        <div class="navbar-brand">
          <a routerLink="/home" class="brand-link">
            <div class="brand-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 21h18"/>
                <path d="M5 21V7l8-4v18"/>
                <path d="M19 21V11l-6-4"/>
                <path d="M9 9v.01"/>
                <path d="M9 12v.01"/>
                <path d="M9 15v.01"/>
                <path d="M9 18v.01"/>
              </svg>
            </div>
            <span class="brand-text">MediCare</span>
          </a>
        </div>

        <!-- Navigation Links -->
        <div class="navbar-nav">
          <a 
            routerLink="/home" 
            routerLinkActive="active" 
            [routerLinkActiveOptions]="{exact: true}"
            class="nav-link"
          >
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9,22 9,12 15,12 15,22"/>
            </svg>
            <span class="nav-text">Home</span>
          </a>
          
          <a 
            routerLink="/appointments/my-appointments" 
            routerLinkActive="active"
            class="nav-link"
          >
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            <span class="nav-text">Appointments</span>
          </a>

          <a 
            routerLink="/complaints" 
            routerLinkActive="active"
            class="nav-link"
          >
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>
              <circle cx="12" cy="13" r="3"/>
            </svg>
            <span class="nav-text">Complaints</span>
          </a>

          <a 
            routerLink="/appointments/schedule" 
            routerLinkActive="active"
            class="nav-link"
          >
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            <span class="nav-text">Book Appointment</span>
          </a>
        </div>

        <!-- User Section -->
        <div class="navbar-user" *ngIf="isLoggedIn()">
          <div class="user-menu">
            <button class="user-button" (click)="toggleUserMenu()">
              <div class="user-avatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
            </button>
            
            <div class="dropdown-menu" [class.show]="showUserMenu">
              <a routerLink="/profile" class="dropdown-item" (click)="closeUserMenu()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
                Profile
              </a>
              <button class="dropdown-item logout" (click)="logout()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                  <polyline points="16,17 21,12 16,7"/>
                  <line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                Logout
              </button>
            </div>
          </div>
        </div>

        <!-- Login/Register Links -->
        <div class="navbar-auth" *ngIf="!isLoggedIn()">
          <a routerLink="/login" class="auth-link login">Login</a>
          <a routerLink="/register" class="auth-link register">Register</a>
        </div>
      </div>
    </nav>
  `,
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  showUserMenu = false;
  currentRoute = '';

  constructor(
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
        this.closeUserMenu();
      });
  }

  shouldHideNavbar(): boolean {
    // Hide navbar on login, register, and admin pages
    return this.currentRoute.includes('/login') ||
      this.currentRoute.includes('/register') ||
      this.currentRoute.includes('/admin');
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getCurrentUser(): any {
    return this.authService.getCurrentUser();
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.router.navigate(['/home']);
      }
    });
  }
}
