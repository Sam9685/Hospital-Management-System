// frontend/hospital-management-frontend/src/app/core/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  console.log('Auth Interceptor - URL:', req.url);
  console.log('Auth Interceptor - Token:', token ? `${token.substring(0, 20)}...` : 'No token');

  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('Auth Interceptor - Adding Authorization header');
    return next(authReq);
  }

  console.log('Auth Interceptor - No token, proceeding without auth');
  return next(req);
};
