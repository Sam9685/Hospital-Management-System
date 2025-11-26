import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  active: boolean;
}

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css'],
  imports: [CommonModule, RouterOutlet],
  standalone: true
})
export class AdminLayoutComponent implements OnInit {
  isSidebarOpen = true;
  currentUser: any = null;
  currentRoute = '';

  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard', active: false },
    { label: 'Users', icon: 'users', route: '/admin/users', active: false },
    { label: 'Doctors', icon: 'doctor', route: '/admin/doctors', active: false },
    { label: 'Appointments', icon: 'calendar', route: '/admin/appointments', active: false },
    { label: 'Complaints', icon: 'complaint', route: '/admin/complaints', active: false },
    // { label: 'Audit Logs', icon: 'audit', route: '/admin/audit-logs', active: false }
  ];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentRoute = event.url;
        this.updateActiveMenu();
      }
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.updateActiveMenu();
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/auth/login']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.router.navigate(['/auth/login']);
      }
    });
  }

  private updateActiveMenu(): void {
    this.menuItems.forEach(item => {
      item.active = this.currentRoute === item.route;
    });
  }

  getPageTitle(): string {
    const activeItem = this.menuItems.find(item => item.active);
    return activeItem ? activeItem.label : 'Dashboard';
  }
}
