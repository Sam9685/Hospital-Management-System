import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminLayoutComponent } from './layout/admin-layout.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard.component';
import { AdminUsersComponent } from './users/admin-users.component';
// import { AdminDoctorsComponent } from './doctors/admin-doctors.component'; // Temporarily removed due to missing file
import { AdminAppointmentsComponent } from './appointments/admin-appointments.component';
import { AdminComplaintsComponent } from './complaints/admin-complaints.component';
import { AdminAuditLogsComponent } from './audit-logs/admin-audit-logs.component';
import { SharedModule } from '../../shared/shared.module';
import { AdminDoctorsComponent } from './doctors/admin-doctors.component';

@NgModule({
  declarations: [
    AdminLayoutComponent,
    AdminDashboardComponent,
    AdminUsersComponent,
    AdminDoctorsComponent,
    AdminAppointmentsComponent,
    AdminComplaintsComponent,
    AdminAuditLogsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AdminRoutingModule,
    SharedModule
  ]
})
export class AdminModule { }
