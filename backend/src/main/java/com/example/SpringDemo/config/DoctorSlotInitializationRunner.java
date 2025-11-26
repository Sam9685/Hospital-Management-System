package com.example.SpringDemo.config;

import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.service.DoctorSlotGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(2) // Run after basic application startup
public class DoctorSlotInitializationRunner implements CommandLineRunner {
    
    @Autowired
    private DoctorSlotGeneratorService doctorSlotGeneratorService;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Value("${app.auto-generate-doctor-slots:true}")
    private boolean autoGenerateSlots;
    
    @Value("${app.slot-generation-days:30}")
    private int slotGenerationDays;
    
    @Override
    public void run(String... args) throws Exception {
        if (!autoGenerateSlots) {
            System.out.println("=== DOCTOR SLOT AUTO-GENERATION DISABLED ===");
            System.out.println("Set 'app.auto-generate-doctor-slots=true' in application.yml to enable");
            return;
        }
        
        System.out.println("=== DOCTOR SLOT INITIALIZATION RUNNER ===");
        System.out.println("Auto-generating doctor slots: " + (autoGenerateSlots ? "ENABLED" : "DISABLED"));
        System.out.println("Slot generation period: " + slotGenerationDays + " days");
        
        try {
            // Check if doctors exist in the database
            List<Doctor> allDoctors = doctorRepository.findAll();
            System.out.println("Found " + allDoctors.size() + " doctors in database");
            
            if (allDoctors.isEmpty()) {
                System.out.println("⚠️  No doctors found in database. Skipping slot generation.");
                System.out.println("💡 Make sure to populate the doctors table first (e.g., run doctor.sql)");
                return;
            }
            
            // Check how many doctors have proper slot configuration
            long doctorsWithSlotConfig = allDoctors.stream()
                .filter(doctor -> doctor.getSlotStartTime() != null && 
                                doctor.getSlotEndTime() != null && 
                                doctor.getAppointmentDuration() != null && 
                                doctor.getWorkingDays() != null)
                .count();
            
            System.out.println("Doctors with complete slot configuration: " + doctorsWithSlotConfig + "/" + allDoctors.size());
            
            if (doctorsWithSlotConfig == 0) {
                System.out.println("⚠️  No doctors have complete slot configuration. Skipping slot generation.");
                System.out.println("💡 Make sure doctors have: slotStartTime, slotEndTime, appointmentDuration, workingDays");
                return;
            }
            
            // Generate slots for all doctors
            System.out.println("🚀 Starting automatic slot generation for next " + slotGenerationDays + " days...");
            
            // Use the existing service but with custom date range
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(slotGenerationDays);
            
            // Generate slots for each doctor individually for better error handling
            int successCount = 0;
            int errorCount = 0;
            
            for (Doctor doctor : allDoctors) {
                try {
                    // Only generate slots for doctors with complete configuration
                    if (doctor.getSlotStartTime() != null && 
                        doctor.getSlotEndTime() != null && 
                        doctor.getAppointmentDuration() != null && 
                        doctor.getWorkingDays() != null) {
                        
                        doctorSlotGeneratorService.generateSlotsForDoctor(doctor, startDate, endDate);
                        successCount++;
                        System.out.println("✅ Generated slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
                    } else {
                        System.out.println("⚠️  Skipping Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + " - incomplete slot configuration");
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("❌ Error generating slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + ": " + e.getMessage());
                }
            }
            
            System.out.println("=== SLOT GENERATION SUMMARY ===");
            System.out.println("✅ Successful: " + successCount + " doctors");
            System.out.println("❌ Errors: " + errorCount + " doctors");
            System.out.println("📅 Period: " + startDate + " to " + endDate);
            System.out.println("=== SLOT INITIALIZATION COMPLETED ===");
            
        } catch (Exception e) {
            System.err.println("❌ Critical error during slot generation: " + e.getMessage());
            e.printStackTrace();
            System.err.println("⚠️  Application will continue to start despite slot generation error");
        }
    }
}
