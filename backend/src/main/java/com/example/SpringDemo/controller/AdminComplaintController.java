package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.ComplaintRequest;
import com.example.SpringDemo.entity.Complaint;
import com.example.SpringDemo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/admin/complaints")
@CrossOrigin(origins = "*")
public class AdminComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Complaint>>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        
        System.out.println("=== ADMIN COMPLAINTS API DEBUG ===");
        System.out.println("Page: " + page + ", Size: " + size);
        System.out.println("Sort: " + sortBy + " " + sortDir);
        System.out.println("Filters - Title: " + title + ", Category: " + category + ", Status: " + status + ", Priority: " + priority);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getAllComplaints(title, category, status, priority, pageable);
        System.out.println("Found " + complaints.getTotalElements() + " complaints");
        
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }
    
    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Complaint>>> getAvailableComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assignedTo) {
        
        System.out.println("=== ADMIN AVAILABLE COMPLAINTS API DEBUG ===");
        System.out.println("Page: " + page + ", Size: " + size);
        System.out.println("Sort: " + sortBy + " " + sortDir);
        System.out.println("Filters - Title: " + title + ", Category: " + category + ", Status: " + status + ", Priority: " + priority + ", AssignedTo: " + assignedTo);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getAvailableComplaints(title, category, status, priority, assignedTo, pageable);
        System.out.println("Found " + complaints.getTotalElements() + " available complaints");
        
        return ResponseEntity.ok(ApiResponse.success(complaints));
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
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Complaint>> updateComplaint(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        try {
            // Create a ComplaintRequest from the update data
            ComplaintRequest request = new ComplaintRequest();
            if (updateData.containsKey("title")) request.setTitle((String) updateData.get("title"));
            if (updateData.containsKey("description")) request.setDescription((String) updateData.get("description"));
            if (updateData.containsKey("category")) request.setCategory((String) updateData.get("category"));
            if (updateData.containsKey("priority")) request.setPriority((String) updateData.get("priority"));
            if (updateData.containsKey("contactPreference")) request.setContactPreference((String) updateData.get("contactPreference"));
            
            Complaint updatedComplaint = complaintService.updateComplaint(id, request);
            return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully", updatedComplaint));
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
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Complaint>> updateComplaintStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            Complaint updatedComplaint = complaintService.updateComplaintStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Complaint status updated successfully", updatedComplaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Complaint>> getComplaintById(@PathVariable Long id) {
        try {
            Complaint complaint = complaintService.getComplaintById(id);
            return ResponseEntity.ok(ApiResponse.success(complaint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignComplaint(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long adminId = request.get("adminId");
            complaintService.assignComplaint(id, adminId);
            return ResponseEntity.ok(ApiResponse.success("Complaint assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/resolution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateComplaintResolution(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            complaintService.updateComplaintResolution(id, request);
            return ResponseEntity.ok(ApiResponse.success("Complaint resolution updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> addComplaintNote(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String note = request.get("note");
            complaintService.addComplaintNote(id, note);
            return ResponseEntity.ok(ApiResponse.success("Note added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
