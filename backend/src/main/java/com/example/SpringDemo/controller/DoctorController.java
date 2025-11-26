package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.DoctorRequest;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.service.AppointmentService;
import com.example.SpringDemo.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@Valid @RequestBody DoctorRequest request) {
        try {
            Doctor doctor = doctorService.createDoctor(request);
            return ResponseEntity.ok(ApiResponse.success("Doctor created successfully", doctor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Doctor>>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Doctor> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Doctor>>> getActiveDoctors() {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctorsBySpecialization(@PathVariable Long specializationId) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specializationId);
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Doctor>>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long specializationId,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) BigDecimal minFee,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Doctor> doctors = doctorService.searchDoctors(name, specializationId, minExperience, 
                                                          maxExperience, minFee, maxFee, pageable);
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        if (doctor.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(doctor.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Doctor not found"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable Long id, 
                                                           @RequestBody Map<String, Object> updateData) {
        try {
            Doctor doctor = doctorService.updateDoctor(id, updateData);
            return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", doctor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteDoctor(@PathVariable Long id) {
        try {
            Map<String, Object> result = doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<Specialization>>> getSpecializations() {
        try {
            List<Specialization> specializations = doctorService.getSpecializations();
            return ResponseEntity.ok(ApiResponse.success(specializations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ===== DOCTOR-SPECIFIC APPOINTMENT ENDPOINTS =====
    
    /**
     * Get all appointments for the current doctor with filters
     */
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getMyAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
                pageable, status, appointmentType, dateFrom, dateTo, search);
            
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }
    
    /**
     * Get a specific appointment by ID for the current doctor
     */
    @GetMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Appointment>> getMyAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentByIdForDoctor(appointmentId);
            return ResponseEntity.ok(ApiResponse.success(appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving appointment: " + e.getMessage()));
        }
    }
    
    /**
     * Update appointment status and notes (for doctor)
     */
    @PutMapping("/appointments/{appointmentId}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> updateData) {
        try {
            String status = updateData.get("status");
            String notes = updateData.get("notes");
            
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Status is required"));
            }
            
            Appointment appointment = appointmentService.updateAppointmentStatusByDoctor(
                appointmentId, status, notes);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment updated successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error updating appointment: " + e.getMessage()));
        }
    }
    
    /**
     * Get today's appointments for the current doctor
     */
    @GetMapping("/appointments/today")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getTodayAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDate today = LocalDate.now();
            Sort sort = Sort.by("appointmentTime").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
                pageable, null, null, today, today, null);
            
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving today's appointments: " + e.getMessage()));
        }
    }
    
    /**
     * Get upcoming appointments for the current doctor
     */
    @GetMapping("/appointments/upcoming")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getUpcomingAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDate today = LocalDate.now();
            Sort sort = Sort.by("appointmentDate", "appointmentTime").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
                pageable, "SCHEDULED", null, today, null, null);
            
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving upcoming appointments: " + e.getMessage()));
        }
    }
    
    /**
     * Get all appointments for the current doctor (main endpoint for doctor appointments page)
     */
    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('DOCTOR')")
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
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
                pageable, status, appointmentType, dateFrom, dateTo, search);
            
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }
}
