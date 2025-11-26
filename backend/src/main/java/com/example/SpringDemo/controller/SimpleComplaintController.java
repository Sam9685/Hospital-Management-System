package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.ComplaintRequest;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.Complaint;
import com.example.SpringDemo.service.AppointmentService;
import com.example.SpringDemo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simple-complaints")
@CrossOrigin(origins = "*")
public class SimpleComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    // Create complaint - NO AUTHENTICATION REQUIRED (for testing)
    @PostMapping
    public ResponseEntity<ApiResponse<Complaint>> createComplaint(@RequestBody Map<String, Object> complaintData) {
        try {
            System.out.println("=== CREATING COMPLAINT ===");
            System.out.println("Complaint data: " + complaintData);
            
            // Create ComplaintRequest from Map
            ComplaintRequest request = new ComplaintRequest();
            
            // Extract and validate required fields
            if (complaintData.containsKey("patientId")) {
                request.setPatientId(Long.valueOf(complaintData.get("patientId").toString()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Patient ID is required"));
            }
            
            if (complaintData.containsKey("appointmentId") && complaintData.get("appointmentId") != null) {
                request.setAppointmentId(Long.valueOf(complaintData.get("appointmentId").toString()));
            }
            
            if (complaintData.containsKey("category")) {
                request.setCategory(complaintData.get("category").toString());
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Category is required"));
            }
            
            if (complaintData.containsKey("title")) {
                request.setTitle(complaintData.get("title").toString());
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Title is required"));
            }
            
            if (complaintData.containsKey("description")) {
                request.setDescription(complaintData.get("description").toString());
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Description is required"));
            }
            
            if (complaintData.containsKey("contactPreference")) {
                request.setContactPreference(complaintData.get("contactPreference").toString());
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Contact preference is required"));
            }
            
            // Priority defaults to MEDIUM
            request.setPriority("MEDIUM");
            
            System.out.println("Created request: " + request);
            
            Complaint complaint = complaintService.createComplaint(request);
            System.out.println("✅ Complaint created successfully: " + complaint.getComplaintId());
            
            return ResponseEntity.ok(ApiResponse.success("Complaint created successfully", complaint));
        } catch (Exception e) {
            System.out.println("❌ Error creating complaint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create complaint: " + e.getMessage()));
        }
    }
    
    // Get past appointments for a patient - NO AUTHENTICATION REQUIRED (for past appointments only)
    @GetMapping("/patient/{patientId}/past-appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPastAppointments(@PathVariable Long patientId) {
        try {
            System.out.println("Getting past appointments for patient: " + patientId);
            List<Appointment> pastAppointments = appointmentService.getPastAppointmentsByPatient(patientId);
            System.out.println("Found " + pastAppointments.size() + " past appointments");
            return ResponseEntity.ok(ApiResponse.success(pastAppointments));
        } catch (Exception e) {
            System.out.println("Error getting past appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get all appointments for a patient - NO AUTHENTICATION REQUIRED (for demonstration purposes)
    @GetMapping("/patient/{patientId}/all-appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments(@PathVariable Long patientId) {
        try {
            System.out.println("Getting all appointments for patient: " + patientId);
            Map<String, Object> allAppointments = appointmentService.getAllAppointmentsByPatient(patientId);
            
            // Combine upcoming and past appointments
            @SuppressWarnings("unchecked")
            List<Appointment> upcoming = (List<Appointment>) allAppointments.get("upcoming");
            @SuppressWarnings("unchecked")
            List<Appointment> past = (List<Appointment>) allAppointments.get("past");
            
            List<Appointment> allAppointmentsList = new java.util.ArrayList<>();
            if (upcoming != null) allAppointmentsList.addAll(upcoming);
            if (past != null) allAppointmentsList.addAll(past);
            
            // Sort by appointment date (most recent first)
            allAppointmentsList.sort((a, b) -> b.getAppointmentDate().compareTo(a.getAppointmentDate()));
            
            System.out.println("Found " + allAppointmentsList.size() + " total appointments (upcoming: " + 
                (upcoming != null ? upcoming.size() : 0) + ", past: " + (past != null ? past.size() : 0) + ")");
            return ResponseEntity.ok(ApiResponse.success(allAppointmentsList));
        } catch (Exception e) {
            System.out.println("Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get complaints by patient - NO AUTHENTICATION REQUIRED
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComplaintsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        try {
            System.out.println("Getting complaints for patient: " + patientId + " page: " + page + " size: " + size + " status: " + status);
            
            // Create Pageable object
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, 
                size, 
                sortDir.equalsIgnoreCase("desc") ? 
                    org.springframework.data.domain.Sort.by(sortBy).descending() : 
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );
            
            // Use the filtering method instead of the simple patient method
            org.springframework.data.domain.Page<Complaint> complaintPage = complaintService.getComplaintsByPatientWithFilters(
                patientId, status, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("complaints", complaintPage.getContent());
            response.put("currentPage", complaintPage.getNumber());
            response.put("totalPages", complaintPage.getTotalPages());
            response.put("totalElements", complaintPage.getTotalElements());
            response.put("size", complaintPage.getSize());
            response.put("first", complaintPage.isFirst());
            response.put("last", complaintPage.isLast());
            response.put("numberOfElements", complaintPage.getNumberOfElements());
            
            System.out.println("Found " + complaintPage.getTotalElements() + " total complaints, showing " + complaintPage.getNumberOfElements() + " on page " + (page + 1));
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            System.out.println("Error getting complaints: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get complaint by ID - NO AUTHENTICATION REQUIRED
    @GetMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<Complaint>> getComplaintById(@PathVariable Long complaintId) {
        try {
            System.out.println("Getting complaint by ID: " + complaintId);
            Complaint complaint = complaintService.getComplaintById(complaintId);
            System.out.println("Found complaint: " + complaint.getTitle());
            return ResponseEntity.ok(ApiResponse.success(complaint));
        } catch (Exception e) {
            System.out.println("Error getting complaint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get complaint categories - NO AUTHENTICATION REQUIRED
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComplaintCategories() {
        try {
            Map<String, Object> categories = new HashMap<>();
            categories.put("categories", Complaint.Category.values());
            categories.put("contactPreferences", Complaint.ContactPreference.values());
            categories.put("priorities", Complaint.Priority.values());
            categories.put("statuses", Complaint.Status.values());
            
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Update customer feedback - NO AUTHENTICATION REQUIRED (for testing)
    @PutMapping("/{complaintId}/feedback")
    public ResponseEntity<ApiResponse<Complaint>> updateCustomerFeedback(@PathVariable Long complaintId, @RequestBody Map<String, Object> feedbackData) {
        try {
            System.out.println("=== UPDATING CUSTOMER FEEDBACK ===");
            System.out.println("Complaint ID: " + complaintId);
            System.out.println("Feedback data: " + feedbackData);
            
            if (!feedbackData.containsKey("customerFeedback")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Customer feedback is required"));
            }
            
            String customerFeedback = feedbackData.get("customerFeedback").toString();
            
            Complaint complaint = complaintService.getComplaintById(complaintId);
            complaint.setCustomerFeedback(customerFeedback);
            complaint.setUpdatedAt(java.time.LocalDateTime.now());
            
            Complaint updatedComplaint = complaintService.updateComplaintFeedback(complaintId, customerFeedback);
            System.out.println("✅ Customer feedback updated successfully for complaint: " + complaintId);
            
            return ResponseEntity.ok(ApiResponse.success("Customer feedback updated successfully", updatedComplaint));
        } catch (Exception e) {
            System.out.println("❌ Error updating customer feedback: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update customer feedback: " + e.getMessage()));
        }
    }
    
    // Test endpoint - NO AUTHENTICATION REQUIRED
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEndpoint() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Simple complaint controller is working");
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
