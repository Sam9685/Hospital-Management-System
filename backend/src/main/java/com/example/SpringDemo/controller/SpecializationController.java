package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/specializations")
@CrossOrigin(origins = "*")
public class SpecializationController {
    
    @Autowired
    private SpecializationService specializationService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Specialization>> createSpecialization(@RequestBody Specialization specialization) {
        try {
            Specialization createdSpecialization = specializationService.createSpecialization(specialization);
            return ResponseEntity.ok(ApiResponse.success("Specialization created successfully", createdSpecialization));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Specialization>>> getAllSpecializations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Specialization> specializations = specializationService.getAllSpecializations(pageable);
        return ResponseEntity.ok(ApiResponse.success(specializations));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Specialization>>> getActiveSpecializations() {
        List<Specialization> specializations = specializationService.getActiveSpecializations();
        return ResponseEntity.ok(ApiResponse.success(specializations));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Specialization>>> searchSpecializations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Specialization.Status statusEnum = status != null ? Specialization.Status.valueOf(status) : null;
        Page<Specialization> specializations = specializationService.searchSpecializations(name, statusEnum, pageable);
        return ResponseEntity.ok(ApiResponse.success(specializations));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Specialization>> getSpecializationById(@PathVariable Long id) {
        Optional<Specialization> specialization = specializationService.getSpecializationById(id);
        if (specialization.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(specialization.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Specialization not found"));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Specialization>> updateSpecialization(@PathVariable Long id, 
                                                                           @RequestBody Specialization specialization) {
        try {
            Specialization updatedSpecialization = specializationService.updateSpecialization(id, specialization);
            return ResponseEntity.ok(ApiResponse.success("Specialization updated successfully", updatedSpecialization));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialization(@PathVariable Long id) {
        try {
            specializationService.deleteSpecialization(id);
            return ResponseEntity.ok(ApiResponse.success("Specialization deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
