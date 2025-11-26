import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  name: string;
  firstname: string;
  lastname: string;
  email: string;
  username: string;
  role: string;
  gender: string;
  birthdate?: string;
  contact?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  countryCode?: string;
  postalCode?: string;
  bloodGroup?: string;
  emergencyContactName?: string;
  emergencyContactNum?: string;
  profileUrl?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface Doctor {
  doctorId: number;
  firstName: string;
  lastName: string;
  email: string;
  contact?: string;
  gender?: string;
  emergencyContactName?: string;
  emergencyContactNum?: string;
  state?: string;
  city?: string;
  address?: string;
  country?: string;
  countryCode?: string;
  postalCode?: string;
  bloodGroup?: string;
  profileUrl?: string;
  specialization: {
    specializationId: number;
    name: string;
  };
  licenseNumber: string;
  yearsOfExp: number;
  qualification: string;
  consultationFee: number;
  status: string;
  joiningDate: string;
  bio?: string;
  active: boolean;
  // Slot management fields
  slotStartTime: string;
  slotEndTime: string;
  appointmentDuration: number;
  workingDays: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface Appointment {
  id: number;
  patient: User;
  doctor: Doctor;
  appointmentDate: string;
  appointmentTime: string;
  endTime: string;
  status: string;
  notes?: string;
  appointmentType?: string;
  symptoms?: string;
  consultationFee?: number;
  cancellationReason?: string;
  cancelledAt?: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface Complaint {
  complaintId: number;
  user?: User;
  title: string;
  description: string;
  category: string;
  status: string;
  priority: string;
  contactPreference?: string;
  response?: string;
  patient?: User;
  assignedTo?: User;
  resolutionNotes?: string;
  resolution?: string;
  customerFeedback?: string;
  appointment?: {
    id: number;
    appointmentDate: string;
    appointmentTime: string;
    doctor: {
      doctorId: number;
      user: {
        firstname: string;
        lastname: string;
      };
      specialization: {
        name: string;
      };
    };
  };
  notes?: ComplaintNote[];
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface ComplaintNote {
  id: number;
  content: string;
  createdAt: string;
  createdBy?: User;
}

export interface Payment {
  id: number;
  paymentId: string;
  appointment: Appointment;
  patient: User;
  amount: number;
  method: string;
  status: string;
  transactionId?: string;
  paymentDate: string;
  cardholderName?: string;
  cardNumber?: string;
  expiryDate?: string;
  cvv?: string;
  billingAddress?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DoctorSlot {
  slotId: number;
  doctor: Doctor;
  slotDate: string;
  startTime: string;
  endTime: string;
  status: string;
}

export interface Specialization {
  specializationId: number;
  name: string;
  description: string;
  status: string;
}

export interface AuditLog {
  id: number;
  user?: User;
  action: string;
  tableName: string;
  recordId?: number;
  oldValues?: string;
  newValues?: string;
  ipAddress?: string;
  userAgent?: string;
  createdAt: string;
}

export interface SearchFilters {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  name?: string;
  email?: string;
  role?: string;
  gender?: string;
  active?: string;
  status?: string;
  patientName?: string;
  doctorName?: string;
  title?: string;
  action?: string;
  specialization?: string;
  appointmentType?: string;
  dateFrom?: string;
  dateTo?: string;
  tableName?: string;
  description?: string;
  userId?: string;
  category?: string;
  priority?: string;
  fromDate?: string;
  toDate?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // User stats
  getUserStats(): Promise<any> {
    return this.http.get(`${this.apiUrl}/api/admin/users/stats`).toPromise();
  }

  // Appointment stats
  getAppointmentStats(): Promise<any> {
    return this.http.get(`${this.apiUrl}/api/admin/appointments/stats`).toPromise();
  }

  // Complaint stats
  getComplaintStats(): Promise<any> {
    return this.http.get(`${this.apiUrl}/api/admin/complaints/stats`).toPromise();
  }

  // Recent audit logs
  getRecentAuditLogs(): Promise<any> {
    return this.http.get(`${this.apiUrl}/api/admin/audit-logs/recent`).toPromise();
  }

  // Users management
  getUsers(filters: SearchFilters): Observable<ApiResponse<PaginatedResponse<User>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.name) params.name = filters.name;
    if (filters.email) params.email = filters.email;
    if (filters.role) params.role = filters.role;
    if (filters.gender) params.gender = filters.gender;
    if (filters.active) params.status = filters.active;
    
    console.log('Fetching users with params:', params);
    return this.http.get<ApiResponse<PaginatedResponse<User>>>(`${this.apiUrl}/api/admin/users`, { params });
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/users/${id}`, userData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/users/${id}`);
  }

  createUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/admin/users`, userData);
  }

  // Doctors management
  getDoctors(filters: SearchFilters): Observable<ApiResponse<PaginatedResponse<Doctor>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.name) params.name = filters.name;
    if (filters.email) params.email = filters.email;
    if (filters.specialization) params.specialization = filters.specialization;
    if (filters.active) params.active = filters.active;
    
    console.log('Fetching doctors with params:', params);
    return this.http.get<ApiResponse<PaginatedResponse<Doctor>>>(`${this.apiUrl}/api/admin/doctors`, { params });
  }

  updateDoctor(id: number, doctorData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/doctors/${id}`, doctorData);
  }

  deleteDoctor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/doctors/${id}`);
  }

  createDoctor(doctorData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/admin/doctors`, doctorData);
  }

  // Appointments management
  getAppointments(filters: SearchFilters): Observable<ApiResponse<PaginatedResponse<Appointment>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.patientName) params.patientName = filters.patientName;
    if (filters.doctorName) params.doctorName = filters.doctorName;
    if (filters.status) params.status = filters.status;
    if (filters.appointmentType) params.appointmentType = filters.appointmentType;
    if (filters.dateFrom) params.dateFrom = filters.dateFrom;
    if (filters.dateTo) params.dateTo = filters.dateTo;
    
    console.log('Fetching appointments with params:', params);
    console.log('Appointments API URL:', `${this.apiUrl}/api/admin/appointments`);
    return this.http.get<ApiResponse<PaginatedResponse<Appointment>>>(`${this.apiUrl}/api/admin/appointments`, { params });
  }

  updateAppointment(id: number, appointmentData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/appointments/${id}`, appointmentData);
  }

  deleteAppointment(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/appointments/${id}`);
  }

  // User appointment methods
  getUserAppointments(userId: number): Observable<{upcoming: Appointment[], past: Appointment[], totalUpcoming: number, totalPast: number}> {
    return this.http.get<ApiResponse<{upcoming: Appointment[], past: Appointment[], totalUpcoming: number, totalPast: number}>>(`${this.apiUrl}/api/appointments/patient/${userId}/all`)
      .pipe(map(response => response.data));
  }

  getUserAppointmentsPaginated(userId: number, page: number = 0, size: number = 10, sortBy: string = 'appointmentDate', sortDir: string = 'desc', status?: string, type?: string): Observable<any> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy,
      sortDir: sortDir
    });
    
    if (status) {
      params.append('status', status);
    }
    
    if (type) {
      params.append('type', type);
    }

    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/api/appointments/patient/${userId}/paginated?${params}`)
      .pipe(map(response => response.data));
  }

  getUpcomingAppointments(userId: number): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/api/appointments/patient/${userId}/upcoming`)
      .pipe(map(response => response.data));
  }

  getPastAppointments(userId: number): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/api/appointments/patient/${userId}/past`)
      .pipe(map(response => response.data));
  }

  cancelAppointment(appointmentId: number, reason: string): Observable<Appointment> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/api/appointments/${appointmentId}/cancel`, null, {
      params: new HttpParams().set('reason', reason)
    }).pipe(map(response => response.data));
  }

  rescheduleAppointment(appointmentId: number, appointmentData: any): Observable<Appointment> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/api/appointments/${appointmentId}/reschedule`, appointmentData)
      .pipe(map(response => response.data));
  }

  // Complaints management
  getComplaints(filters: SearchFilters): Observable<ApiResponse<PaginatedResponse<Complaint>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.title) params.title = filters.title;
    if (filters.category) params.category = filters.category;
    if (filters.status) params.status = filters.status;
    if (filters.priority) params.priority = filters.priority;
    
    console.log('Fetching complaints with params:', params);
    console.log('Complaints API URL:', `${this.apiUrl}/api/admin/complaints`);
    return this.http.get<ApiResponse<PaginatedResponse<Complaint>>>(`${this.apiUrl}/api/admin/complaints`, { params });
  }

  getComplaintsAvailableToAdmin(filters: any): Observable<ApiResponse<PaginatedResponse<Complaint>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.assignedTo) params.assignedTo = filters.assignedTo;
    
    console.log('Fetching available complaints with params:', params);
    return this.http.get<ApiResponse<PaginatedResponse<Complaint>>>(`${this.apiUrl}/api/admin/complaints/available`, { params });
  }

  updateComplaint(id: number, complaintData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/complaints/${id}`, complaintData);
  }

  deleteComplaint(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/complaints/${id}`);
  }

  // Audit logs management
  getAuditLogs(filters: SearchFilters): Observable<ApiResponse<PaginatedResponse<AuditLog>>> {
    let params: any = { 
      page: filters.page, 
      size: filters.size 
    };
    if (filters.sortBy) params.sortBy = filters.sortBy;
    if (filters.sortDir) params.sortDir = filters.sortDir;
    if (filters.action) params.action = filters.action;
    if (filters.tableName) params.tableName = filters.tableName;
    if (filters.userId) params.userId = filters.userId;
    if (filters.fromDate) params.fromDate = filters.fromDate;
    if (filters.toDate) params.toDate = filters.toDate;
    if (filters.status) params.status = filters.status;
    
    console.log('Fetching audit logs with params:', params);
    console.log('Audit logs API URL:', `${this.apiUrl}/api/admin/audit-logs`);
    return this.http.get<ApiResponse<PaginatedResponse<AuditLog>>>(`${this.apiUrl}/api/admin/audit-logs`, { params });
  }

  // Additional methods
  updateComplaintStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/complaints/${id}/status`, { status });
  }

  // Doctor creation - Get patients using admin users API with role filter
  getPatients(searchTerm: string = ''): Observable<ApiResponse<PaginatedResponse<User>>> {
    const params = {
      page: '0',
      size: '1000', // Get all patients
      role: 'PATIENT',
      name: searchTerm
    };
    return this.http.get<ApiResponse<PaginatedResponse<User>>>(`${this.apiUrl}/api/admin/users`, { params });
  }

  getSpecializations(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/api/doctors/specializations`);
  }

  // Complaint details
  getComplaintById(id: number): Observable<ApiResponse<Complaint>> {
    return this.http.get<ApiResponse<Complaint>>(`${this.apiUrl}/api/admin/complaints/${id}`);
  }

  getAdmins(): Observable<ApiResponse<User[]>> {
    return this.http.get<ApiResponse<User[]>>(`${this.apiUrl}/api/admin/users/admins`);
  }

  assignComplaint(complaintId: number, adminId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/complaints/${complaintId}/assign`, { adminId });
  }

  updateComplaintResolution(complaintId: number, resolutionData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/admin/complaints/${complaintId}/resolution`, resolutionData);
  }

  addComplaintNote(complaintId: number, note: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/admin/complaints/${complaintId}/notes`, { note });
  }

  // Appointment Scheduling Methods
  getAppointmentSpecializations(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/appointments/specializations`);
  }

  getAvailableSlots(specializationId: number, date: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/appointments/available-slots?specializationId=${specializationId}&date=${date}`);
  }

  getDoctorSlots(doctorId: number, date: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/appointments/doctors/${doctorId}/slots?date=${date}`);
  }

  bookAppointment(bookingData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/appointments`, bookingData);
  }

  generateSlots(): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/appointments/generate-slots`, {});
  }

  // Payment methods
  createPayment(paymentData: any): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(`${this.apiUrl}/api/payments`, paymentData);
  }

  getPaymentById(paymentId: string): Observable<Payment> {
    return this.http.get<ApiResponse<Payment>>(`${this.apiUrl}/api/payments/payment-id/${paymentId}`)
      .pipe(map(response => response.data));
  }

  getPaymentsByPatient(patientId: number): Observable<Payment[]> {
    return this.http.get<ApiResponse<Payment[]>>(`${this.apiUrl}/api/payments/patient/${patientId}`)
      .pipe(map(response => response.data));
  }

  linkPaymentToAppointment(paymentId: string, appointmentId: number): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(`${this.apiUrl}/api/payments/${paymentId}/link-appointment/${appointmentId}`, {});
  }

  // New payment-first flow methods
  createPaymentWithAppointment(paymentData: any): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(`${this.apiUrl}/api/payments/with-appointment`, paymentData);
  }

  confirmPaymentAndCreateAppointment(paymentId: string): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/api/payments/${paymentId}/confirm`, {});
  }
}