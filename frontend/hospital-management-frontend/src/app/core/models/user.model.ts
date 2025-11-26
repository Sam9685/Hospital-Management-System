// frontend/hospital-management-frontend/src/app/core/models/user.model.ts
export interface User {
  id: number;
  name: string;
  firstname: string;
  lastname: string;
  username: string;
  email: string;
  contact: string;
  countryCode: string;
  address: string;
  role: 'ADMIN' | 'SUPERADMIN' | 'PATIENT' | 'DOCTOR';
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  state?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  bloodGroup?: string;
  emergencyContactName?: string;
  emergencyContactNum?: string;
  profileUrl?: string;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role: string;
  name: string;
}

export interface PatientRegistrationRequest {
  name: string;
  firstname: string;
  lastname: string;
  email: string;
  contact: string;
  countryCode: string;
  address: string;
  username: string;
  password: string;
  confirmPassword: string;
  state?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  bloodGroup?: string;
  emergencyContactName?: string;
  emergencyContactNum?: string;
}
