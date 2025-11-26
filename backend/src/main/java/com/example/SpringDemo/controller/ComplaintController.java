package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.ComplaintRequest;
import com.example.SpringDemo.entity.Complaint;
import com.example.SpringDemo.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Complaint>> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        try {
            Complaint complaint = complaintService.createComplaint(request);
            return ResponseEntity.ok(ApiResponse.success("Complaint created successfully", complaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Complaint>>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long assignedTo) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getAllComplaints(
            category, status, priority, patientId, assignedTo, pageable);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Page<Complaint>>> getComplaintsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getComplaintsByPatient(patientId, pageable);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Complaint>>> searchComplaints(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.searchComplaints(
            title, description, category, status, priority, patientId, assignedTo, pageable);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Complaint>> getComplaintById(@PathVariable Long id) {
        Optional<Complaint> complaint = Optional.ofNullable(complaintService.getComplaintById(id));
        if (complaint.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(complaint.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Complaint not found"));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Complaint>> updateComplaint(@PathVariable Long id, 
                                                                 @Valid @RequestBody ComplaintRequest request) {
        try {
            Complaint complaint = complaintService.updateComplaint(id, request);
            return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully", complaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Complaint>> updateComplaintStatus(
            @PathVariable Long id, 
            @RequestParam String status,
            @RequestParam(required = false) String resolutionNotes) {
        try {
            Complaint complaint = complaintService.updateComplaintStatus(id, status, resolutionNotes);
            return ResponseEntity.ok(ApiResponse.success("Complaint status updated successfully", complaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignComplaint(
            @PathVariable Long id, 
            @RequestParam Long assignedTo) {
        try {
            complaintService.assignComplaint(id, assignedTo);
            return ResponseEntity.ok(ApiResponse.success("Complaint assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok(ApiResponse.success("Complaint deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getComplaintStats() {
        try {
            Object stats = complaintService.getComplaintStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<Complaint>> updateCustomerFeedback(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String feedback = request.get("feedback");
            if (feedback == null || feedback.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Feedback is required"));
            }
            Complaint complaint = complaintService.updateCustomerFeedback(id, feedback);
            return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully", complaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
