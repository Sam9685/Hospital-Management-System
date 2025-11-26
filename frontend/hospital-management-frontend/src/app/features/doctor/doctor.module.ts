import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DoctorRoutingModule } from './doctor-routing.module';
import { DoctorLayoutComponent } from './layout/doctor-layout.component';
import { DoctorDashboardComponent } from './dashboard/doctor-dashboard.component';
import { DoctorAppointmentsComponent } from './appointments/doctor-appointments.component';

@NgModule({
  declarations: [
    DoctorLayoutComponent,
    DoctorDashboardComponent,
    DoctorAppointmentsComponent
  ],
  imports: [
    CommonModule,
    DoctorRoutingModule
  ]
})
export class DoctorModule { }
