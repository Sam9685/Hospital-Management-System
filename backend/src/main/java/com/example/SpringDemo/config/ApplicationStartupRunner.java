package com.example.SpringDemo.config;

import com.example.SpringDemo.service.DoctorSlotGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run early in the startup process
public class ApplicationStartupRunner implements CommandLineRunner {
    
    @Autowired
    private DoctorSlotGeneratorService doctorSlotGeneratorService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== APPLICATION STARTUP RUNNER ===");
        System.out.println("Starting automatic doctor slot generation...");
        
        try {
            // Generate slots for all doctors for the next month
            doctorSlotGeneratorService.generateSlotsForAllDoctors();
            System.out.println("✅ Doctor slot generation completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error during doctor slot generation: " + e.getMessage());
            e.printStackTrace();
            // Don't stop application startup if slot generation fails
            System.err.println("⚠️  Application will continue to start despite slot generation error");
        }
        
        System.out.println("=== STARTUP RUNNER COMPLETED ===");
    }
}
