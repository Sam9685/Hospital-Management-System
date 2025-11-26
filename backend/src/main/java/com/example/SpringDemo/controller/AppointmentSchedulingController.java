package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.DoctorSlot;
import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.service.AppointmentService;
import com.example.SpringDemo.service.DoctorSlotService;
import com.example.SpringDemo.service.DoctorService;
import com.example.SpringDemo.service.DoctorSlotGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentSchedulingController {
    
    @Autowired
    private DoctorSlotService doctorSlotService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DoctorSlotGeneratorService doctorSlotGeneratorService;
    
    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<Specialization>>> getSpecializations() {
        try {
            List<Specialization> specializations = doctorService.getSpecializations();
            return ResponseEntity.ok(ApiResponse.success(specializations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<DoctorSlot>>> getAvailableSlots(
            @RequestParam(required = false) Long specializationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DoctorSlot> slots;
            if (specializationId != null) {
                slots = doctorSlotService.getAvailableSlotsBySpecializationAndDate(specializationId, date);
            } else {
                slots = doctorSlotService.getAvailableSlotsByDate(date);
            }
            return ResponseEntity.ok(ApiResponse.success(slots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<ApiResponse<List<DoctorSlot>>> getDoctorSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DoctorSlot> slots = doctorSlotService.getAvailableSlotsByDoctorAndDate(doctorId, date);
            return ResponseEntity.ok(ApiResponse.success(slots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookAppointment(@RequestBody Map<String, Object> bookingData) {
        try {
            System.out.println("=== BOOKING REQUEST DEBUG ===");
            System.out.println("Received booking data: " + bookingData);
            System.out.println("Data type: " + bookingData.getClass().getName());
            System.out.println("Data size: " + bookingData.size());
            
            // Log each key-value pair
            for (Map.Entry<String, Object> entry : bookingData.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue() + ", Type: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null"));
            }
            
            // Validate required fields
            if (!bookingData.containsKey("slotId")) {
                System.err.println("ERROR: slotId is missing");
                return ResponseEntity.badRequest().body(ApiResponse.error("slotId is required"));
            }
            if (!bookingData.containsKey("patientId")) {
                System.err.println("ERROR: patientId is missing");
                return ResponseEntity.badRequest().body(ApiResponse.error("patientId is required"));
            }
            if (!bookingData.containsKey("symptoms")) {
                System.err.println("ERROR: symptoms is missing");
                return ResponseEntity.badRequest().body(ApiResponse.error("symptoms is required"));
            }
            
            // Check for null values
            if (bookingData.get("slotId") == null) {
                System.err.println("ERROR: slotId is null");
                return ResponseEntity.badRequest().body(ApiResponse.error("slotId cannot be null"));
            }
            if (bookingData.get("patientId") == null) {
                System.err.println("ERROR: patientId is null");
                return ResponseEntity.badRequest().body(ApiResponse.error("patientId cannot be null"));
            }
            if (bookingData.get("symptoms") == null) {
                System.err.println("ERROR: symptoms is null");
                return ResponseEntity.badRequest().body(ApiResponse.error("symptoms cannot be null"));
            }
            
            Long slotId = Long.valueOf(bookingData.get("slotId").toString());
            Long patientId = Long.valueOf(bookingData.get("patientId").toString());
            String symptoms = bookingData.get("symptoms").toString();
            String notes = bookingData.get("notes") != null ? bookingData.get("notes").toString() : "";
            
            System.out.println("Parsed data - slotId: " + slotId + ", patientId: " + patientId + ", symptoms: " + symptoms);
            
            // Book the slot
            DoctorSlot slot = doctorSlotService.bookSlot(slotId);
            System.out.println("Slot booked successfully: " + slot.getSlotId());
            
            // Create appointment
            Map<String, Object> appointment = appointmentService.createAppointmentFromSlot(slot, patientId, symptoms, notes);
            System.out.println("Appointment created successfully: " + appointment.get("appointmentId"));
            
            return ResponseEntity.ok(ApiResponse.success(appointment));
        } catch (NumberFormatException e) {
            System.err.println("Number format error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid number format: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Booking error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/generate-slots")
    public ResponseEntity<ApiResponse<String>> generateSlots() {
        try {
            doctorSlotService.generateSlotsForNextMonth();
            return ResponseEntity.ok(ApiResponse.success("Slots generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/ensure-slot-coverage")
    public ResponseEntity<ApiResponse<String>> ensureSlotCoverage() {
        try {
            doctorSlotGeneratorService.ensureSlotCoverage();
            return ResponseEntity.ok(ApiResponse.success("Slot coverage ensured successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/debug/slots")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> debugSlots() {
        try {
            List<DoctorSlot> slots = doctorSlotService.getAllSlots();
            List<Map<String, Object>> slotData = slots.stream()
                .map(slot -> {
                    Map<String, Object> data = new java.util.HashMap<>();
                    data.put("slotId", slot.getSlotId());
                    data.put("doctorId", slot.getDoctor().getDoctorId());
                    data.put("doctorName", slot.getDoctor().getFullName());
                    data.put("specialization", slot.getDoctor().getSpecialization().getName());
                    data.put("slotDate", slot.getSlotDate());
                    data.put("startTime", slot.getStartTime());
                    data.put("endTime", slot.getEndTime());
                    data.put("status", slot.getStatus());
                    return data;
                })
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(slotData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
