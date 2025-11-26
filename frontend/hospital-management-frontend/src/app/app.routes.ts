// frontend/hospital-management-frontend/src/app/app.routes.ts
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { DoctorGuard } from './core/guards/doctor.guard';
import { PatientGuard } from './core/guards/patient.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'auth/login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'auth/register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
  { path: 'login', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: 'register', redirectTo: '/auth/register', pathMatch: 'full' },
  { 
    path: 'home', 
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'appointments', 
    loadComponent: () => import('./features/appointments/appointments.component').then(m => m.AppointmentsComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'appointments/my-appointments', 
    loadComponent: () => import('./features/appointments/my-appointments/my-appointments.component').then(m => m.MyAppointmentsComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'appointments/schedule', 
    loadComponent: () => import('./features/appointments/schedule-appointment/schedule-appointment.component').then(m => m.ScheduleAppointmentComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'payments', 
    loadComponent: () => import('./features/payments/payments.component').then(m => m.PaymentsComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'payments/select', 
    loadComponent: () => import('./features/payments/payment-select/payment-select.component').then(m => m.PaymentSelectComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'payments/form', 
    loadComponent: () => import('./features/payments/payment-form/payment-form.component').then(m => m.PaymentFormComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'payments/success', 
    loadComponent: () => import('./features/payments/payment-success/payment-success.component').then(m => m.PaymentSuccessComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'complaints', 
    loadComponent: () => import('./features/complaints/complaints.component').then(m => m.ComplaintsComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'complaints/tracking', 
    loadComponent: () => import('./features/complaints/complaint-tracking/complaint-tracking.component').then(m => m.ComplaintTrackingComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'profile', 
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [PatientGuard]
  },
  { 
    path: 'admin', 
    loadChildren: () => import('./features/admin/admin-routing.module').then(m => m.AdminRoutingModule),
    canActivate: [AdminGuard]
  },
  { 
    path: 'doctor', 
    loadChildren: () => import('./features/doctor/doctor-routing.module').then(m => m.DoctorRoutingModule),
    canActivate: [DoctorGuard]
  },
  { path: '**', redirectTo: '/home' }
];