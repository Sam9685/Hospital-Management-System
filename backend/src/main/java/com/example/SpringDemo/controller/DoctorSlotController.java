package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.service.DoctorSlotGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor-slots")
@CrossOrigin(origins = "*")
public class DoctorSlotController {
    
    @Autowired
    private DoctorSlotGeneratorService doctorSlotGeneratorService;
    
    /**
     * Generate slots for all doctors for the next month
     */
    @PostMapping("/generate-all")
    public ResponseEntity<ApiResponse<String>> generateAllSlots() {
        try {
            doctorSlotGeneratorService.generateSlotsForAllDoctors();
            return ResponseEntity.ok(ApiResponse.success("Slots generated successfully for all doctors"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generating slots: " + e.getMessage()));
        }
    }
    
    /**
     * Generate slots for the next week
     */
    @PostMapping("/generate-next-week")
    public ResponseEntity<ApiResponse<String>> generateNextWeekSlots() {
        try {
            doctorSlotGeneratorService.generateNextWeekSlots();
            return ResponseEntity.ok(ApiResponse.success("Next week slots generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generating next week slots: " + e.getMessage()));
        }
    }
    
    /**
     * Generate slots for all doctors for the next month (for initial setup)
     */
    @PostMapping("/generate-initial-slots")
    public ResponseEntity<ApiResponse<String>> generateInitialSlots() {
        try {
            doctorSlotGeneratorService.generateSlotsForAllDoctors();
            return ResponseEntity.ok(ApiResponse.success("Initial slots generated successfully for all doctors"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generating initial slots: " + e.getMessage()));
        }
    }
    
    /**
     * Generate slots for all doctors for a specific number of days (configurable)
     */
    @PostMapping("/generate-slots")
    public ResponseEntity<ApiResponse<String>> generateSlots(@RequestParam(defaultValue = "30") int days) {
        try {
            System.out.println("=== MANUAL SLOT GENERATION REQUEST ===");
            System.out.println("Requested days: " + days);
            
            doctorSlotGeneratorService.generateSlotsForAllDoctors();
            return ResponseEntity.ok(ApiResponse.success("Slots generated successfully for all doctors for the next " + days + " days"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generating slots: " + e.getMessage()));
        }
    }
}