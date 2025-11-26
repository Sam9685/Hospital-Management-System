package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.AppointmentRequest;
import com.example.SpringDemo.dto.AppointmentDetailsResponse;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Appointment>> createAppointment(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== APPOINTMENT CREATION DEBUG ===");
            System.out.println("Received appointment request: " + requestData);
            
            // Extract and validate required fields
            Long patientId = extractLong(requestData, "patientId");
            Long doctorId = extractLong(requestData, "doctorId");
            String appointmentDateStr = extractString(requestData, "appointmentDate");
            Map<String, Object> appointmentTimeObj = extractTimeObject(requestData, "appointmentTime");
            Map<String, Object> endTimeObj = extractTimeObject(requestData, "endTime");
            String appointmentType = extractString(requestData, "appointmentType");
            Object consultationFeeObj = requestData.get("consultationFee");
            String symptoms = extractString(requestData, "symptoms");
            String notes = extractString(requestData, "notes");
            
            System.out.println("Extracted data:");
            System.out.println("Patient ID: " + patientId);
            System.out.println("Doctor ID: " + doctorId);
            System.out.println("Appointment Date: " + appointmentDateStr);
            System.out.println("Appointment Time: " + appointmentTimeObj);
            System.out.println("End Time: " + endTimeObj);
            System.out.println("Appointment Type: " + appointmentType);
            System.out.println("Consultation Fee: " + consultationFeeObj);
            System.out.println("Symptoms: " + symptoms);
            System.out.println("Notes: " + notes);
            
            // Validate required fields
            if (patientId == null) {
                throw new RuntimeException("Patient ID is required");
            }
            if (doctorId == null) {
                throw new RuntimeException("Doctor ID is required");
            }
            if (appointmentDateStr == null) {
                throw new RuntimeException("Appointment date is required");
            }
            if (appointmentTimeObj == null) {
                throw new RuntimeException("Appointment time is required");
            }
            if (endTimeObj == null) {
                throw new RuntimeException("End time is required");
            }
            if (appointmentType == null || appointmentType.trim().isEmpty()) {
                throw new RuntimeException("Appointment type is required");
            }
            if (consultationFeeObj == null) {
                throw new RuntimeException("Consultation fee is required");
            }
            
            // Create AppointmentRequest object
            AppointmentRequest request = new AppointmentRequest();
            request.setPatientId(patientId);
            request.setDoctorId(doctorId);
            request.setAppointmentDate(java.time.LocalDate.parse(appointmentDateStr));
            request.setAppointmentTime(parseTimeFromObject(appointmentTimeObj));
            request.setEndTime(parseTimeFromObject(endTimeObj));
            request.setAppointmentType(appointmentType);
            request.setConsultationFee(new java.math.BigDecimal(consultationFeeObj.toString()));
            request.setSymptoms(symptoms);
            request.setNotes(notes);
            
            Appointment appointment = appointmentService.createAppointment(request);
            System.out.println("Appointment created successfully with ID: " + appointment.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Appointment created successfully", appointment));
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creating appointment: " + e.getMessage()));
        }
    }
    
    // Helper methods
    private Long extractLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(value.toString());
    }
    
    private String extractString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractTimeObject(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
    
    private java.time.LocalTime parseTimeFromObject(Map<String, Object> timeObj) {
        if (timeObj == null) return null;
        
        int hour = ((Number) timeObj.get("hour")).intValue();
        int minute = ((Number) timeObj.get("minute")).intValue();
        int second = timeObj.containsKey("second") ? ((Number) timeObj.get("second")).intValue() : 0;
        int nano = timeObj.containsKey("nano") ? ((Number) timeObj.get("nano")).intValue() : 0;
        
        return java.time.LocalTime.of(hour, minute, second, nano);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getAppointmentsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId, pageable);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getAppointmentsByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/doctor/my-appointments")
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
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Appointment> appointments = appointmentService.getMyAppointmentsAsDoctor(
            pageable, status, appointmentType, dateFrom, dateTo, search);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Appointment>>> searchAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Appointment> appointments = appointmentService.searchAppointments(patientId, doctorId, status, 
                                                                              appointmentType, fromDate, toDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(appointment.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Appointment not found"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(@PathVariable Long id, 
                                                                     @RequestBody Map<String, Object> updateData) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, updateData);
            return ResponseEntity.ok(ApiResponse.success("Appointment updated successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointment(@PathVariable Long id, 
                                                                     @RequestParam String reason) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id, reason);
            return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Appointment>> completeAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.completeAppointment(id);
            return ResponseEntity.ok(ApiResponse.success("Appointment completed successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<AppointmentDetailsResponse>> getAppointmentDetails(@PathVariable Long id) {
        try {
            AppointmentDetailsResponse appointment = appointmentService.getAppointmentDetails(id);
            return ResponseEntity.ok(ApiResponse.success("Appointment details retrieved successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<ApiResponse<List<Appointment>>> getUpcomingAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getUpcomingAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/patient/{patientId}/past")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPastAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getPastAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/patient/{patientId}/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllAppointmentsByPatient(@PathVariable Long patientId) {
        Map<String, Object> appointments = appointmentService.getAllAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }
    
    @GetMapping("/patient/{patientId}/paginated")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentsByPatientPaginated(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        try {
            System.out.println("Getting paginated appointments for patient: " + patientId + " page: " + page + " size: " + size + " status: " + status + " type: " + type);
            
            // Create Pageable object
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, 
                size, 
                sortDir.equalsIgnoreCase("desc") ? 
                    org.springframework.data.domain.Sort.by(sortBy).descending() : 
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );
            
            Map<String, Object> appointments = appointmentService.getAppointmentsByPatientPaginatedWithDateFilter(patientId, status, type, pageable);
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            System.out.println("Error getting paginated appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<Appointment>> rescheduleAppointment(@PathVariable Long id, 
                                                                          @RequestBody Map<String, Object> requestData) {
        try {
            LocalDate newDate = LocalDate.parse(requestData.get("appointmentDate").toString());
            LocalTime newTime = parseTimeFromObject(extractTimeObject(requestData, "appointmentTime"));
            LocalTime newEndTime = parseTimeFromObject(extractTimeObject(requestData, "endTime"));
            
            Appointment appointment = appointmentService.rescheduleAppointment(id, newDate, newTime, newEndTime);
            return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
