import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';

interface DashboardStats {
  totalUsers: number;
  totalDoctors: number;
  totalAppointments: number;
  totalComplaints: number;
  activeUsers: number;
  pendingAppointments: number;
  openComplaints: number;
  recentAuditLogs: any[];
}

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
  change?: number;
  changeType?: 'increase' | 'decrease';
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [CommonModule],
  standalone: true
})
export class AdminDashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalUsers: 0,
    totalDoctors: 0,
    totalAppointments: 0,
    totalComplaints: 0,
    activeUsers: 0,
    pendingAppointments: 0,
    openComplaints: 0,
    recentAuditLogs: []
  };

  statCards: StatCard[] = [];
  isLoading = true;
  recentActivities: any[] = [];
  
  // Make Math available in template
  Math = Math;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    
    // Load all stats in parallel
    Promise.all([
      this.adminService.getUserStats(),
      this.adminService.getAppointmentStats(),
      this.adminService.getComplaintStats(),
      this.adminService.getRecentAuditLogs()
    ]).then(([userStats, appointmentStats, complaintStats, auditLogs]) => {
      this.stats = {
        totalUsers: userStats.totalUsers || 0,
        totalDoctors: userStats.totalDoctors || 0,
        activeUsers: userStats.activeUsers || 0,
        totalAppointments: appointmentStats.totalAppointments || 0,
        pendingAppointments: appointmentStats.pendingAppointments || 0,
        totalComplaints: complaintStats.totalComplaints || 0,
        openComplaints: complaintStats.openComplaints || 0,
        recentAuditLogs: auditLogs || []
      };

      this.updateStatCards();
      this.processRecentActivities();
      this.isLoading = false;
    }).catch(error => {
      console.error('Error loading dashboard data:', error);
      this.isLoading = false;
    });
  }

  private updateStatCards(): void {
    this.statCards = [
      {
        title: 'Total Users',
        value: this.stats.totalUsers,
        icon: 'users',
        color: 'blue',
        change: 12,
        changeType: 'increase'
      },
      {
        title: 'Active Doctors',
        value: this.stats.totalDoctors,
        icon: 'doctor',
        color: 'green',
        change: 5,
        changeType: 'increase'
      },
      {
        title: 'Total Appointments',
        value: this.stats.totalAppointments,
        icon: 'calendar',
        color: 'purple',
        change: 8,
        changeType: 'increase'
      },
      {
        title: 'Open Complaints',
        value: this.stats.openComplaints,
        icon: 'complaint',
        color: 'red',
        change: -2,
        changeType: 'decrease'
      },
      {
        title: 'Active Users',
        value: this.stats.activeUsers,
        icon: 'user-check',
        color: 'indigo',
        change: 15,
        changeType: 'increase'
      },
      {
        title: 'Pending Appointments',
        value: this.stats.pendingAppointments,
        icon: 'clock',
        color: 'yellow',
        change: 3,
        changeType: 'increase'
      }
    ];
  }

  private processRecentActivities(): void {
    this.recentActivities = this.stats.recentAuditLogs.slice(0, 10).map(log => ({
      id: log.id,
      action: log.action,
      tableName: log.tableName,
      user: log.user?.name || 'System',
      timestamp: new Date(log.createdAt),
      description: this.getActivityDescription(log)
    }));
  }

  private getActivityDescription(log: any): string {
    const action = log.action?.toLowerCase();
    const table = log.tableName?.toLowerCase();
    
    switch (action) {
      case 'insert':
        return `New ${table} created`;
      case 'update':
        return `${table} updated`;
      case 'delete':
        return `${table} deleted`;
      case 'select':
        return `${table} accessed`;
      default:
        return `${action} performed on ${table}`;
    }
  }

  getTimeAgo(timestamp: Date): string {
    const now = new Date();
    const diff = now.getTime() - timestamp.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    return `${days}d ago`;
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}
