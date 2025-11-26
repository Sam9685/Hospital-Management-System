import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DoctorLayoutComponent } from './layout/doctor-layout.component';
import { DoctorDashboardComponent } from './dashboard/doctor-dashboard.component';
import { DoctorAppointmentsComponent } from './appointments/doctor-appointments.component';
import { DoctorGuard } from '../../core/guards/doctor.guard';

const routes: Routes = [
  {
    path: '',
    component: DoctorLayoutComponent,
    canActivate: [DoctorGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: DoctorDashboardComponent
      },
      {
        path: 'appointments',
        component: DoctorAppointmentsComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DoctorRoutingModule { }
