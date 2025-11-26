package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.Complaint;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.repository.ComplaintRepository;
import com.example.SpringDemo.repository.AppointmentRepository;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enhanced-data-generation")
@CrossOrigin(origins = "*")
public class EnhancedDataGenerationController {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @PostMapping("/generate-comprehensive-complaints")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateComprehensiveComplaints() {
        try {
            System.out.println("=== GENERATING COMPREHENSIVE TEST COMPLAINTS ===");
            
            // Get users and appointments for testing
            List<User> users = userRepository.findAll();
            List<Appointment> appointments = appointmentRepository.findAll();
            
            if (users.isEmpty() || appointments.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("No users or appointments found. Please create some test data first."));
            }
            
            Map<String, Object> result = new HashMap<>();
            int complaintsCreated = 0;
            
            // Create comprehensive test complaints with different scenarios
            String[][] complaintData = {
                // Open complaints
                {"TREATMENT_ISSUE", "Doctor was late for appointment", "The doctor arrived 30 minutes late for my scheduled appointment, causing inconvenience and delay in my treatment.", "EMAIL", "MEDIUM", "OPEN"},
                {"SERVICE_ISSUE", "Poor customer service at reception", "The reception staff was rude and unhelpful when I tried to reschedule my appointment. They did not provide clear information about available slots.", "CALL", "HIGH", "OPEN"},
                {"BILLING_ISSUE", "Incorrect billing amount charged", "I was charged double the consultation fee mentioned during booking. The billing department is not responding to my calls.", "EMAIL", "HIGH", "OPEN"},
                {"STAFF_ISSUE", "Nurse was unprofessional during treatment", "The nurse assigned to my treatment was unprofessional and made inappropriate comments. This affected my overall experience.", "SMS", "MEDIUM", "OPEN"},
                {"FACILITY_ISSUE", "Air conditioning not working in waiting area", "The air conditioning in the waiting area has been broken for weeks, making it very uncomfortable for patients.", "EMAIL", "LOW", "OPEN"},
                
                // In Progress complaints
                {"TREATMENT_ISSUE", "Medication side effects not explained", "I experienced side effects from the prescribed medication that were not explained to me beforehand. Need better communication about potential risks.", "CALL", "MEDIUM", "IN_PROGRESS"},
                {"SERVICE_ISSUE", "Long waiting time for lab results", "Lab results took 5 days instead of the promised 2 days. This delayed my treatment plan.", "EMAIL", "MEDIUM", "IN_PROGRESS"},
                {"BILLING_ISSUE", "Insurance claim processing delay", "My insurance claim has been pending for over a month without any updates from the billing department.", "EMAIL", "HIGH", "IN_PROGRESS"},
                
                // Resolved complaints
                {"STAFF_ISSUE", "Doctor did not listen to my concerns", "The doctor seemed rushed and did not properly listen to my symptoms and concerns during the consultation.", "CALL", "MEDIUM", "RESOLVED"},
                {"FACILITY_ISSUE", "Parking space shortage", "There are not enough parking spaces available, especially during peak hours. Had to park far away and walk.", "SMS", "LOW", "RESOLVED"},
                
                // Closed complaints
                {"TREATMENT_ISSUE", "Wrong diagnosis provided", "I believe I was given an incorrect diagnosis which led to unnecessary treatment and additional costs.", "CALL", "CRITICAL", "CLOSED"},
                {"SERVICE_ISSUE", "Previous complaint not properly addressed", "My previous complaint about billing was marked as resolved but the issue still persists. Need to reopen this case.", "EMAIL", "HIGH", "CLOSED"},
                
                // Reopened complaint
                {"OTHER", "General feedback about hospital services", "Overall experience at the hospital has been declining. Need better coordination between departments and improved patient care.", "EMAIL", "MEDIUM", "REOPENED"},
                
                // Multiple complaints from same patient
                {"BILLING_ISSUE", "Duplicate charges on credit card", "I noticed duplicate charges on my credit card statement for the same consultation fee.", "CALL", "HIGH", "OPEN"},
                {"FACILITY_ISSUE", "Cleanliness issues in restroom", "The patient restroom on the second floor was not clean and lacked basic amenities like soap and paper towels.", "EMAIL", "LOW", "IN_PROGRESS"},
                
                // Additional variety
                {"TREATMENT_ISSUE", "Follow-up appointment not scheduled", "The doctor promised to schedule a follow-up appointment but I never received any communication about it.", "EMAIL", "MEDIUM", "OPEN"},
                {"SERVICE_ISSUE", "Online portal not working", "The patient portal is not working properly and I cannot access my medical records or test results.", "EMAIL", "HIGH", "IN_PROGRESS"},
                {"BILLING_ISSUE", "Payment method not accepted", "The hospital's payment system rejected my valid credit card multiple times, causing delays in treatment.", "CALL", "MEDIUM", "RESOLVED"},
                {"STAFF_ISSUE", "Security guard was intimidating", "The security guard at the entrance was unnecessarily intimidating and made me feel uncomfortable.", "SMS", "LOW", "CLOSED"},
                {"FACILITY_ISSUE", "Elevator out of order", "The main elevator has been out of order for 3 days, making it difficult for elderly patients to access upper floors.", "EMAIL", "MEDIUM", "OPEN"}
            };
            
            for (int i = 0; i < complaintData.length && i < users.size(); i++) {
                User user = users.get(i % users.size());
                Appointment appointment = appointments.size() > i ? appointments.get(i % appointments.size()) : null;
                
                Complaint complaint = new Complaint();
                complaint.setPatient(user);
                complaint.setAppointment(appointment);
                
                // Set complaint data
                complaint.setCategory(Complaint.Category.valueOf(complaintData[i][0]));
                complaint.setTitle(complaintData[i][1]);
                complaint.setDescription(complaintData[i][2]);
                complaint.setContactPreference(Complaint.ContactPreference.valueOf(complaintData[i][3]));
                complaint.setPriority(Complaint.Priority.valueOf(complaintData[i][4]));
                complaint.setStatus(Complaint.Status.valueOf(complaintData[i][5]));
                
                // Set creation time (spread over different dates)
                complaint.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
                complaint.setCreatedBy(user.getId());
                
                // Add resolution for resolved/closed complaints
                if (complaint.getStatus() == Complaint.Status.RESOLVED || complaint.getStatus() == Complaint.Status.CLOSED) {
                    complaint.setResolution("Issue has been addressed and resolved. Thank you for your feedback.");
                    complaint.setResolutionNotes("Complaint resolved through proper investigation and corrective action.");
                    complaint.setUpdatedAt(LocalDateTime.now().minusDays(i % 15));
                } else if (complaint.getStatus() == Complaint.Status.IN_PROGRESS) {
                    complaint.setUpdatedAt(LocalDateTime.now().minusDays(i % 7));
                }
                
                complaintRepository.save(complaint);
                complaintsCreated++;
                
                System.out.println("Created complaint: " + complaint.getTitle() + " (Status: " + complaint.getStatus() + ", Priority: " + complaint.getPriority() + ")");
            }
            
            result.put("message", "Comprehensive test complaints generated successfully");
            result.put("complaintsCreated", complaintsCreated);
            result.put("totalUsers", users.size());
            result.put("totalAppointments", appointments.size());
            
            System.out.println("✅ Generated " + complaintsCreated + " comprehensive test complaints");
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.out.println("❌ Error generating comprehensive test complaints: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate comprehensive test complaints: " + e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEndpoint() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Enhanced data generation controller is working");
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
