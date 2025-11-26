import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  duration?: number;
  actions?: ToastAction[];
}

export interface ToastAction {
  label: string;
  action: () => void;
  style?: 'primary' | 'secondary';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
  public toasts$ = this.toastsSubject.asObservable();

  showSuccess(message: string, actions?: ToastAction[], duration: number = 5000): string {
    return this.show(message, 'success', actions, duration);
  }

  showError(message: string, actions?: ToastAction[], duration: number = 7000): string {
    return this.show(message, 'error', actions, duration);
  }

  showWarning(message: string, actions?: ToastAction[], duration: number = 6000): string {
    return this.show(message, 'warning', actions, duration);
  }

  showInfo(message: string, actions?: ToastAction[], duration: number = 5000): string {
    return this.show(message, 'info', actions, duration);
  }

  private show(message: string, type: ToastMessage['type'], actions?: ToastAction[], duration: number = 5000): string {
    const id = this.generateId();
    const toast: ToastMessage = {
      id,
      message,
      type,
      duration,
      actions
    };

    const currentToasts = this.toastsSubject.value;
    this.toastsSubject.next([...currentToasts, toast]);

    // Auto remove after duration
    if (duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, duration);
    }

    return id;
  }

  remove(id: string): void {
    const currentToasts = this.toastsSubject.value;
    this.toastsSubject.next(currentToasts.filter(toast => toast.id !== id));
  }

  clear(): void {
    this.toastsSubject.next([]);
  }

  private generateId(): string {
    return Math.random().toString(36).substr(2, 9);
  }
}
