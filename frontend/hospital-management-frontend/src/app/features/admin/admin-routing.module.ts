import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from './layout/admin-layout.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard.component';
import { AdminUsersComponent } from './users/admin-users.component';
// import { AdminDoctorsComponent } from './doctors/admin-doctors.component'; // Removed due to missing module
import { AdminAppointmentsComponent } from './appointments/admin-appointments.component';
import { AdminComplaintsComponent } from './complaints/admin-complaints.component';
import { AdminAuditLogsComponent } from './audit-logs/admin-audit-logs.component';
import { ComplaintDetailsComponent } from './complaint-details/complaint-details.component';
import { AdminGuard } from '../../core/guards/admin.guard';
import { AdminDoctorsComponent } from './doctors/admin-doctors.component';

const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [AdminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'doctors', component: AdminDoctorsComponent },
      { path: 'appointments', component: AdminAppointmentsComponent },
      { path: 'complaints', component: AdminComplaintsComponent },
      { path: 'complaints/:id', component: ComplaintDetailsComponent },
      { path: 'audit-logs', component: AdminAuditLogsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
