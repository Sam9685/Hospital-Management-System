package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.DoctorSlot;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.repository.DoctorSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DoctorSlotGeneratorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DoctorSlotRepository doctorSlotRepository;
    
    /**
     * Generate slots for all active doctors for the next month
     */
    public void generateSlotsForAllDoctors() {
        System.out.println("=== GENERATING SLOTS FOR ALL DOCTORS ===");
        
        List<Doctor> activeDoctors = doctorRepository.findAllActive();
        System.out.println("Found " + activeDoctors.size() + " active doctors");
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        
        for (Doctor doctor : activeDoctors) {
            try {
                generateSlotsForDoctor(doctor, startDate, endDate);
                System.out.println("Generated slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
            } catch (Exception e) {
                System.err.println("Error generating slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== SLOT GENERATION COMPLETED ===");
    }
    
    /**
     * Generate slots for a specific doctor for the next day
     */
    public void generateSlotsForDoctorNextDay(Doctor doctor) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        generateSlotsForDoctor(doctor, tomorrow, tomorrow);
    }
    
    
    /**
     * Generate slots for a specific date
     */
    private List<DoctorSlot> generateSlotsForDate(Doctor doctor, LocalDate date, LocalTime startTime, 
                                                 LocalTime endTime, Integer durationMinutes) {
        List<DoctorSlot> slots = new ArrayList<>();
        
        LocalTime currentTime = startTime;
        
        while (currentTime.plusMinutes(durationMinutes).isBefore(endTime) || 
               currentTime.plusMinutes(durationMinutes).equals(endTime)) {
            
            DoctorSlot slot = new DoctorSlot();
            slot.setDoctor(doctor);
            slot.setSlotDate(date);
            slot.setStartTime(currentTime);
            slot.setEndTime(currentTime.plusMinutes(durationMinutes));
            slot.setStatus(DoctorSlot.SlotStatus.AVAILABLE);
            slot.setCreatedBy(1L); // System user
            
            slots.add(slot);
            
            currentTime = currentTime.plusMinutes(durationMinutes);
        }
        
        return slots;
    }
    
    /**
     * Parse working days string to DayOfWeek list
     */
    private List<DayOfWeek> parseWorkingDays(String workingDaysStr) {
        List<DayOfWeek> workingDays = new ArrayList<>();
        
        String[] days = workingDaysStr.split(",");
        for (String day : days) {
            day = day.trim();
            switch (day) {
                case "MONDAY":
                    workingDays.add(DayOfWeek.MONDAY);
                    break;
                case "TUESDAY":
                    workingDays.add(DayOfWeek.TUESDAY);
                    break;
                case "WEDNESDAY":
                    workingDays.add(DayOfWeek.WEDNESDAY);
                    break;
                case "THURSDAY":
                    workingDays.add(DayOfWeek.THURSDAY);
                    break;
                case "FRIDAY":
                    workingDays.add(DayOfWeek.FRIDAY);
                    break;
                case "SATURDAY":
                    workingDays.add(DayOfWeek.SATURDAY);
                    break;
                case "SUNDAY":
                    workingDays.add(DayOfWeek.SUNDAY);
                    break;
            }
        }
        
        return workingDays;
    }
    
    /**
     * Cron job to maintain advance slot availability
     * Runs every day at 00:01 AM
     * Ensures all doctors have slots available for the next month
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void maintainAdvanceSlots() {
        System.out.println("=== CRON JOB: Maintaining advance slot availability ===");
        
        // Use the new ensureSlotCoverage method for better efficiency
        ensureSlotCoverage();
        
        System.out.println("=== CRON JOB COMPLETED ===");
    }
    
    /**
     * Generate slots for tomorrow at midnight (legacy method for backward compatibility)
     * This is now handled by maintainAdvanceSlots()
     */
    @Deprecated
    public void generateTomorrowSlots() {
        maintainAdvanceSlots();
    }
    
    
    /**
     * Generate slots for next 7 days (for immediate use)
     */
    public void generateNextWeekSlots() {
        System.out.println("=== GENERATING NEXT WEEK SLOTS ===");
        
        List<Doctor> activeDoctors = doctorRepository.findAllActive();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        for (Doctor doctor : activeDoctors) {
            try {
                generateSlotsForDoctor(doctor, startDate, endDate);
            } catch (Exception e) {
                System.err.println("Error generating next week slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== NEXT WEEK SLOT GENERATION COMPLETED ===");
    }
    
    /**
     * Generate slots for a specific doctor for the next month (used by DoctorService)
     * This is called when a new doctor is created - generates 1 month of slots in advance
     */
    public void generateSlotsForDoctor(Doctor doctor) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        System.out.println("=== INITIAL SLOT GENERATION FOR NEW DOCTOR ===");
        System.out.println("Doctor: " + doctor.getFirstName() + " " + doctor.getLastName());
        System.out.println("Generating slots from " + startDate + " to " + endDate);
        generateSlotsForDoctor(doctor, startDate, endDate);
    }
    
    /**
     * Handle working days update for a doctor
     * Working days changes will take effect after the current slot period (next month)
     * This ensures existing appointments are not affected
     */
    public void handleWorkingDaysUpdate(Doctor doctor) {
        System.out.println("=== HANDLING WORKING DAYS UPDATE ===");
        System.out.println("Doctor: " + doctor.getFirstName() + " " + doctor.getLastName());
        System.out.println("New working days: " + doctor.getWorkingDays());
        System.out.println("Note: Working days changes will take effect after current slot period");
        
        // The actual slot generation with new working days will happen in the next cron job
        // or when the current slot period expires. This method just logs the change.
        System.out.println("Working days update logged. Changes will be applied in next slot generation cycle.");
    }
    
    /**
     * Check and ensure all doctors have sufficient slot coverage
     * This method ensures we always have slots available for the next month
     */
    public void ensureSlotCoverage() {
        System.out.println("=== ENSURING SLOT COVERAGE ===");
        
        List<Doctor> activeDoctors = doctorRepository.findAllActive();
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusMonths(1);
        
        System.out.println("Checking slot coverage for " + activeDoctors.size() + " active doctors");
        System.out.println("Target coverage: " + today + " to " + targetDate);
        
        for (Doctor doctor : activeDoctors) {
            try {
                // Check if doctor has slots for the target period
                boolean hasSlots = doctorSlotRepository.existsByDoctorAndSlotDate(doctor, targetDate);
                
                if (!hasSlots) {
                    System.out.println("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " needs slot coverage");
                    generateSlotsForDoctor(doctor, today, targetDate);
                } else {
                    System.out.println("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " has sufficient slot coverage");
                }
            } catch (Exception e) {
                System.err.println("Error checking slot coverage for Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== SLOT COVERAGE CHECK COMPLETED ===");
    }
    
    /**
     * Generate slots for a specific doctor for the next month (used by DoctorService)
     * This is called when a new doctor is created - generates 1 month of slots in advance
     */
    public void generateSlotsForDoctor(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        System.out.println("=== SLOT GENERATION FOR DOCTOR ===");
        System.out.println("Doctor: " + doctor.getFirstName() + " " + doctor.getLastName());
        System.out.println("Generating slots from " + startDate + " to " + endDate);
        
        // Parse doctor's working hours and days
        String startTimeStr = doctor.getSlotStartTime();
        String endTimeStr = doctor.getSlotEndTime();
        Integer durationMinutes = doctor.getAppointmentDuration();
        String workingDaysStr = doctor.getWorkingDays();
        
        if (startTimeStr == null || endTimeStr == null || durationMinutes == null || workingDaysStr == null) {
            System.err.println("Doctor " + doctor.getEmail() + " has incomplete slot configuration");
            return;
        }
        
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        List<DayOfWeek> workingDays = parseWorkingDays(workingDaysStr);
        
        List<DoctorSlot> slotsToCreate = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // Check if doctor works on this day
            if (workingDays.contains(dayOfWeek)) {
                // Check if slots already exist for this doctor and date
                if (!doctorSlotRepository.existsByDoctorAndSlotDate(doctor, currentDate)) {
                    List<DoctorSlot> daySlots = generateSlotsForDate(doctor, currentDate, startTime, endTime, durationMinutes);
                    slotsToCreate.addAll(daySlots);
                } else {
                    System.out.println("Slots already exist for Dr. " + doctor.getFirstName() + " on " + currentDate);
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        if (!slotsToCreate.isEmpty()) {
            doctorSlotRepository.saveAll(slotsToCreate);
            System.out.println("Created " + slotsToCreate.size() + " slots for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        } else {
            System.out.println("No new slots created for Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        }
    }
}