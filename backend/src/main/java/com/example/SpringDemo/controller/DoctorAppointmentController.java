package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
public class DoctorAppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * Get appointments for the current logged-in doctor
     */
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getMyAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String search) {
        
        try {
            // Get current doctor from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
            }
            
            String email = authentication.getName();
            System.out.println("Doctor email from authentication: " + email);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
                pageable, status, appointmentType, dateFrom, dateTo, search);
            
            return ResponseEntity.ok(ApiResponse.success(appointments));
            
        } catch (Exception e) {
            System.err.println("Error getting doctor appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Error getting appointments: " + e.getMessage()));
        }
    }
    
    /**
     * Update appointment status (for doctor to mark as completed/cancelled)
     */
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        
        try {
            // Get current doctor from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
            }
            
            Appointment updatedAppointment = appointmentService.updateAppointmentStatusByDoctor(
                appointmentId, status, notes);
            
            return ResponseEntity.ok(ApiResponse.success(updatedAppointment));
            
        } catch (Exception e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Error updating appointment: " + e.getMessage()));
        }
    }
    
    /**
     * Get appointment details for a specific appointment
     */
    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentDetails(@PathVariable Long appointmentId) {
        try {
            // Get current doctor from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
            }
            
            Appointment appointment = appointmentService.getAppointmentByIdForDoctor(appointmentId);
            
            return ResponseEntity.ok(ApiResponse.success(appointment));
            
        } catch (Exception e) {
            System.err.println("Error getting appointment details: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Error getting appointment: " + e.getMessage()));
        }
    }
}
