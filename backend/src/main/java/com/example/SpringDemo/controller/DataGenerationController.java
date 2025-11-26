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
@RequestMapping("/api/data-generation")
@CrossOrigin(origins = "*")
public class DataGenerationController {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @PostMapping("/generate-complaints")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateTestComplaints() {
        try {
            System.out.println("=== GENERATING TEST COMPLAINTS ===");
            
            // Get some users and appointments for testing
            List<User> users = userRepository.findAll();
            List<Appointment> appointments = appointmentRepository.findAll();
            
            if (users.isEmpty() || appointments.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("No users or appointments found. Please create some test data first."));
            }
            
            Map<String, Object> result = new HashMap<>();
            int complaintsCreated = 0;
            
            // Create test complaints with different statuses
            for (int i = 0; i < Math.min(10, users.size()); i++) {
                User user = users.get(i);
                Appointment appointment = appointments.size() > i ? appointments.get(i) : null;
                
                Complaint complaint = new Complaint();
                complaint.setPatient(user);
                complaint.setAppointment(appointment);
                
                // Set different categories
                Complaint.Category[] categories = Complaint.Category.values();
                complaint.setCategory(categories[i % categories.length]);
                
                // Set different titles and descriptions
                String[] titles = {
                    "Doctor was late for appointment",
                    "Poor customer service at reception", 
                    "Incorrect billing amount charged",
                    "Nurse was unprofessional during treatment",
                    "Air conditioning not working in waiting area",
                    "Medication side effects not explained",
                    "Long waiting time for lab results",
                    "Insurance claim processing delay",
                    "Doctor did not listen to my concerns",
                    "Parking space shortage"
                };
                
                String[] descriptions = {
                    "The doctor arrived 30 minutes late for my scheduled appointment, causing inconvenience and delay in my treatment.",
                    "The reception staff was rude and unhelpful when I tried to reschedule my appointment.",
                    "I was charged double the consultation fee mentioned during booking.",
                    "The nurse assigned to my treatment was unprofessional and made inappropriate comments.",
                    "The air conditioning in the waiting area has been broken for weeks.",
                    "I experienced side effects from the prescribed medication that were not explained to me beforehand.",
                    "Lab results took 5 days instead of the promised 2 days.",
                    "My insurance claim has been pending for over a month without any updates.",
                    "The doctor seemed rushed and did not properly listen to my symptoms.",
                    "There are not enough parking spaces available, especially during peak hours."
                };
                
                complaint.setTitle(titles[i % titles.length]);
                complaint.setDescription(descriptions[i % descriptions.length]);
                
                // Set different contact preferences
                Complaint.ContactPreference[] contactPrefs = Complaint.ContactPreference.values();
                complaint.setContactPreference(contactPrefs[i % contactPrefs.length]);
                
                // Set different priorities
                Complaint.Priority[] priorities = Complaint.Priority.values();
                complaint.setPriority(priorities[i % priorities.length]);
                
                // Set different statuses
                Complaint.Status[] statuses = Complaint.Status.values();
                complaint.setStatus(statuses[i % statuses.length]);
                
                // Set creation time (spread over different dates)
                complaint.setCreatedAt(LocalDateTime.now().minusDays(i));
                complaint.setCreatedBy(user.getId());
                
                // Add resolution for resolved/closed complaints
                if (complaint.getStatus() == Complaint.Status.RESOLVED || complaint.getStatus() == Complaint.Status.CLOSED) {
                    complaint.setResolution("Issue has been addressed and resolved. Thank you for your feedback.");
                    complaint.setResolutionNotes("Complaint resolved through proper investigation and corrective action.");
                }
                
                complaintRepository.save(complaint);
                complaintsCreated++;
                
                System.out.println("Created complaint: " + complaint.getTitle() + " (Status: " + complaint.getStatus() + ")");
            }
            
            result.put("message", "Test complaints generated successfully");
            result.put("complaintsCreated", complaintsCreated);
            result.put("totalUsers", users.size());
            result.put("totalAppointments", appointments.size());
            
            System.out.println("✅ Generated " + complaintsCreated + " test complaints");
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.out.println("❌ Error generating test complaints: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate test complaints: " + e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEndpoint() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Data generation controller is working");
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}