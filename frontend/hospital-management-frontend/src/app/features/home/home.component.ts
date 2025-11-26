import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatMenuModule,
    MatDividerModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  currentUser: any = null;
  isLoading = false;
  isMenuOpen = false;

  // Quick actions for patients
  patientActions = [
    // {
    //   title: 'Book Appointment',
    //   description: 'Schedule a new appointment with a doctor',
    //   icon: 'event',
    //   route: '/appointments/book',
    //   color: 'primary'
    // },
    {
      title: 'My Appointments',
      description: 'Check your upcoming and past appointments',
      icon: 'schedule',
      route: '/appointments/my-appointments',
      color: 'accent'
    },
    {
      title: 'Schedule Appointment',
      description: 'Book a new appointment with our doctors',
      icon: 'add_circle',
      route: 'appointments/schedule',
      color: 'primary'
    },
    // {
    //   title: 'Make Payment',
    //   description: 'Pay for your appointments and services',
    //   icon: 'payment',
    //   route: '/payments',
    //   color: 'warn'
    // },
    {
      title: 'Register Complaint',
      description: 'Submit feedback or complaints',
      icon: 'feedback',
      route: '/complaints',
      color: 'primary'
    },
    {
      title: 'Update Profile',
      description: 'Manage your personal information',
      icon: 'person',
      route: '/profile',
      color: 'accent'
    }
  ];

  // Quick actions for admin
  adminActions = [
    {
      title: 'Manage Doctors',
      description: 'Add, edit, or remove doctors',
      icon: 'medical_services',
      route: '/admin/doctors',
      color: 'primary'
    },
    {
      title: 'View Appointments',
      description: 'Manage all appointments',
      icon: 'schedule',
      route: '/admin/appointments',
      color: 'accent'
    },
    {
      title: 'Handle Complaints',
      description: 'Review and resolve complaints',
      icon: 'support_agent',
      route: '/admin/complaints',
      color: 'warn'
    },
    {
      title: 'Dashboard',
      description: 'View system analytics and reports',
      icon: 'dashboard',
      route: '/admin/dashboard',
      color: 'primary'
    }
  ];

  // Statistics for dashboard
  stats = [
    {
      title: 'Total Appointments',
      value: '156',
      change: '+12%',
      changeType: 'positive',
      icon: 'event'
    },
    {
      title: 'Active Patients',
      value: '1,234',
      change: '+8%',
      changeType: 'positive',
      icon: 'people'
    },
    {
      title: 'Available Doctors',
      value: '24',
      change: '+2',
      changeType: 'positive',
      icon: 'medical_services'
    },
    {
      title: 'Pending Complaints',
      value: '7',
      change: '-3',
      changeType: 'negative',
      icon: 'feedback'
    }
  ];

  // Recent activities
  recentActivities = [
    {
      type: 'appointment',
      title: 'New appointment booked',
      description: 'Dr. Smith - Cardiology',
      time: '2 hours ago',
      icon: 'event'
    },
    {
      type: 'payment',
      title: 'Payment received',
      description: '₹150.00 - Consultation fee',
      time: '4 hours ago',
      icon: 'payment'
    },
    {
      type: 'complaint',
      title: 'New complaint submitted',
      description: 'Service quality issue',
      time: '6 hours ago',
      icon: 'feedback'
    },
    {
      type: 'profile',
      title: 'Profile updated',
      description: 'Contact information changed',
      time: '1 day ago',
      icon: 'person'
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadUserData();
  }

  loadUserData() {
    this.isLoading = true;
    this.currentUser = this.authService.getCurrentUser();
    this.isLoading = false;
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.snackBar.open('Logged out successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.snackBar.open('Logged out successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.router.navigate(['/login']);
      }
    });
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  getQuickActions() {
    if (this.currentUser?.role === 'ADMIN') {
      return this.adminActions;
    }
    return this.patientActions;
  }

  getGreeting() {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
  }

  getUserDisplayName() {
    if (this.currentUser) {
      return this.currentUser.firstname || this.currentUser.name || 'User';
    }
    return 'Guest';
  }

  getUserRole() {
    if (this.currentUser?.role === 'ADMIN') {
      return 'Administrator';
    }
    return 'Patient';
  }

  getActionColorClass(color: string) {
    const colorMap: { [key: string]: string } = {
      'primary': 'action-primary',
      'accent': 'action-accent',
      'warn': 'action-warn'
    };
    return colorMap[color] || 'action-primary';
  }

  getStatChangeClass(changeType: string) {
    return changeType === 'positive' ? 'stat-positive' : 'stat-negative';
  }

  getActivityIconClass(type: string) {
    const iconMap: { [key: string]: string } = {
      'appointment': 'activity-appointment',
      'payment': 'activity-payment',
      'complaint': 'activity-complaint',
      'profile': 'activity-profile'
    };
    return iconMap[type] || 'activity-default';
  }
}