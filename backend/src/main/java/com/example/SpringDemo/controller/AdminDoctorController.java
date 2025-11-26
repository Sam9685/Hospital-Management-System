package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.DoctorRequest;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doctors")
@CrossOrigin(origins = "*")
public class AdminDoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Doctor>>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "doctorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long specialization,
            @RequestParam(required = false) String active) {
        
        System.out.println("=== ADMIN DOCTORS API DEBUG ===");
        System.out.println("Page: " + page + ", Size: " + size);
        System.out.println("Sort: " + sortBy + " " + sortDir);
        System.out.println("Filters - Name: " + name + ", Email: " + email + ", Specialization: " + specialization + ", Active: " + active);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Doctor> doctors = doctorService.getAllDoctors(name, email, specialization, active, pageable);
        System.out.println("Found " + doctors.getTotalElements() + " doctors");
        
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getDoctorStats() {
        try {
            Object stats = doctorService.getDoctorStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(id, updateData);
            return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", updatedDoctor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteDoctor(@PathVariable Long id) {
        try {
            Map<String, Object> result = doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@RequestBody DoctorRequest request) {
        try {
            System.out.println("Received doctor request: " + request); // Debug log
            Doctor doctor = doctorService.createDoctor(request);
            return ResponseEntity.ok(ApiResponse.success("Doctor created successfully", doctor));
        } catch (Exception e) {
            System.out.println("Error creating doctor: " + e.getMessage()); // Debug log
            e.printStackTrace(); // Debug stack trace
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/specializations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Specialization>>> getSpecializations() {
        try {
            List<Specialization> specializations = doctorService.getSpecializations();
            System.out.println("Returning specializations: " + specializations.size() + " items");
            for (Specialization spec : specializations) {
                System.out.println("Specialization: " + spec.getName() + " - ID: " + spec.getSpecializationId());
            }
            return ResponseEntity.ok(ApiResponse.success(specializations));
        } catch (Exception e) {
            System.out.println("Error getting specializations: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
}
