import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface DoctorStats {
  todaysAppointments: number;
  totalPatients: number;
  completedAppointments: number;
  pendingAppointments: number;
}

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
  change?: number;
  changeType?: 'increase' | 'decrease';
}

interface Appointment {
  id: number;
  patientName: string;
  time: string;
  type: string;
  status: string;
}

@Component({
  selector: 'app-doctor-dashboard',
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.component.css'],
  imports: [CommonModule],
  standalone: true
})
export class DoctorDashboardComponent implements OnInit {
  currentUser: any = null;
  stats: DoctorStats = {
    todaysAppointments: 0,
    totalPatients: 0,
    completedAppointments: 0,
    pendingAppointments: 0
  };

  statCards: StatCard[] = [];
  isLoading = true;
  todaysAppointments: Appointment[] = [];
  
  // Make Math available in template
  Math = Math;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    
    // Mock data for now - in real implementation, this would come from API
    setTimeout(() => {
      this.stats = {
        todaysAppointments: 8,
        totalPatients: 156,
        completedAppointments: 142,
        pendingAppointments: 14
      };

      this.todaysAppointments = [
        {
          id: 1,
          patientName: 'John Smith',
          time: '09:00 AM',
          type: 'Follow-up',
          status: 'Confirmed'
        },
        {
          id: 2,
          patientName: 'Sarah Johnson',
          time: '10:30 AM',
          type: 'Consultation',
          status: 'Confirmed'
        },
        {
          id: 3,
          patientName: 'Mike Brown',
          time: '02:00 PM',
          type: 'Check-up',
          status: 'Confirmed'
        },
        {
          id: 4,
          patientName: 'Emily Davis',
          time: '03:30 PM',
          type: 'Follow-up',
          status: 'Pending'
        }
      ];

      this.updateStatCards();
      this.isLoading = false;
    }, 1000);
  }

  private updateStatCards(): void {
    this.statCards = [
      {
        title: "Today's Appointments",
        value: this.stats.todaysAppointments,
        icon: 'calendar',
        color: 'blue',
        change: 2,
        changeType: 'increase'
      },
      {
        title: 'Total Patients',
        value: this.stats.totalPatients,
        icon: 'users',
        color: 'green',
        change: 8,
        changeType: 'increase'
      },
      {
        title: 'Completed Appointments',
        value: this.stats.completedAppointments,
        icon: 'check-circle',
        color: 'purple',
        change: 5,
        changeType: 'increase'
      },
      {
        title: 'Pending Appointments',
        value: this.stats.pendingAppointments,
        icon: 'clock',
        color: 'yellow',
        change: -1,
        changeType: 'decrease'
      }
    ];
  }

  navigateToAppointments(): void {
    this.router.navigate(['/doctor/appointments']);
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}
