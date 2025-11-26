package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.DoctorSlot;
import com.example.SpringDemo.entity.DoctorSlotTemplate;
import com.example.SpringDemo.repository.DoctorSlotRepository;
import com.example.SpringDemo.repository.DoctorSlotTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DoctorSlotService {
    
    @Autowired
    private DoctorSlotRepository doctorSlotRepository;
    
    @Autowired
    private DoctorSlotTemplateRepository doctorSlotTemplateRepository;
    
    public List<DoctorSlot> generateSlotsForDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        List<DoctorSlot> generatedSlots = new ArrayList<>();
        
        // Get all active templates for this doctor
        List<DoctorSlotTemplate> templates = doctorSlotTemplateRepository.findByDoctorDoctorIdAndIsActiveTrue(doctorId);
        
        if (templates.isEmpty()) {
            return generatedSlots;
        }
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // Find templates for this day of week
            List<DoctorSlotTemplate> dayTemplates = templates.stream()
                .filter(template -> template.getDayOfWeek() == dayOfWeek)
                .toList();
            
            for (DoctorSlotTemplate template : dayTemplates) {
                List<DoctorSlot> daySlots = generateSlotsForTemplate(template, currentDate);
                generatedSlots.addAll(daySlots);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        // Save all generated slots
        return doctorSlotRepository.saveAll(generatedSlots);
    }
    
    private List<DoctorSlot> generateSlotsForTemplate(DoctorSlotTemplate template, LocalDate slotDate) {
        List<DoctorSlot> slots = new ArrayList<>();
        
        LocalTime currentTime = template.getStartTime();
        LocalTime endTime = template.getEndTime();
        int slotDuration = template.getSlotDurationMinutes();
        
        while (currentTime.plusMinutes(slotDuration).isBefore(endTime) || 
               currentTime.plusMinutes(slotDuration).equals(endTime)) {
            
            // Check if slot already exists
            boolean slotExists = doctorSlotRepository.existsByDoctorDoctorIdAndSlotDateAndStartTimeAndStatus(
                template.getDoctor().getDoctorId(), 
                slotDate, 
                currentTime, 
                DoctorSlot.SlotStatus.AVAILABLE
            );
            
            if (!slotExists) {
                DoctorSlot slot = new DoctorSlot();
                slot.setDoctor(template.getDoctor());
                slot.setSlotDate(slotDate);
                slot.setStartTime(currentTime);
                slot.setEndTime(currentTime.plusMinutes(slotDuration));
                slot.setStatus(DoctorSlot.SlotStatus.AVAILABLE);
                slot.setCreatedBy(1L); // System user
                
                slots.add(slot);
            }
            
            currentTime = currentTime.plusMinutes(slotDuration);
        }
        
        return slots;
    }
    
    public List<DoctorSlot> getAvailableSlotsByDoctorAndDate(Long doctorId, LocalDate slotDate) {
        return doctorSlotRepository.findAvailableSlotsByDoctorAndDate(doctorId, slotDate);
    }
    
    public List<DoctorSlot> getAvailableSlotsBySpecializationAndDate(Long specializationId, LocalDate slotDate) {
        // If it's today, filter out past time slots
        if (slotDate.equals(LocalDate.now())) {
            return doctorSlotRepository.findAvailableSlotsByDateAndSpecializationWithTimeFilter(slotDate, specializationId, LocalTime.now());
        }
        return doctorSlotRepository.findAvailableSlotsByDateAndSpecialization(slotDate, specializationId);
    }
    
    public List<DoctorSlot> getAvailableSlotsByDate(LocalDate slotDate) {
        // If it's today, filter out past time slots
        if (slotDate.equals(LocalDate.now())) {
            return doctorSlotRepository.findAvailableSlotsByDateWithTimeFilter(slotDate, LocalTime.now());
        }
        return doctorSlotRepository.findAvailableSlotsByDate(slotDate);
    }
    
    public DoctorSlot bookSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot is not available for booking");
        }
        
        slot.setStatus(DoctorSlot.SlotStatus.BOOKED);
        slot.setUpdatedBy(1L); // Current user ID
        
        return doctorSlotRepository.save(slot);
    }
    
    public DoctorSlot cancelSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.BOOKED) {
            throw new RuntimeException("Slot is not booked");
        }
        
        slot.setStatus(DoctorSlot.SlotStatus.CANCELLED);
        slot.setUpdatedBy(1L); // Current user ID
        
        return doctorSlotRepository.save(slot);
    }
    
    public void generateSlotsForNextMonth() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);
        
        // Get all doctors
        // This would typically be injected from DoctorService
        // For now, we'll generate for doctors with IDs 1-10
        for (long doctorId = 1; doctorId <= 10; doctorId++) {
            try {
                generateSlotsForDoctor(doctorId, today, endDate);
            } catch (Exception e) {
                // Log error but continue with other doctors
                System.err.println("Error generating slots for doctor " + doctorId + ": " + e.getMessage());
            }
        }
    }
    
    // Additional methods for DoctorSlotController
    public Page<DoctorSlot> getAllSlots(Long doctorId, String status, LocalDate slotDate, Pageable pageable) {
        // This is a simplified implementation - in a real app, you'd use a more complex query
        return doctorSlotRepository.findAll(pageable);
    }
    
    public Page<DoctorSlot> getSlotsByDoctor(Long doctorId, String status, LocalDate slotDate, Pageable pageable) {
        // This is a simplified implementation - in a real app, you'd use a more complex query
        return doctorSlotRepository.findAll(pageable);
    }
    
    public List<DoctorSlot> getAvailableSlots(Long doctorId, LocalDate slotDate) {
        return doctorSlotRepository.findAvailableSlotsByDoctorAndDate(doctorId, slotDate);
    }
    
    public Page<DoctorSlot> searchSlots(Long doctorId, String status, LocalDate fromDate, LocalDate toDate, 
                                       String startTime, String endTime, Pageable pageable) {
        // This is a simplified implementation - in a real app, you'd use a more complex query
        return doctorSlotRepository.findAll(pageable);
    }
    
    public Optional<DoctorSlot> getSlotById(Long id) {
        return doctorSlotRepository.findById(id);
    }
    
    // Methods required by AppointmentSchedulingController
    public Optional<DoctorSlot> getDoctorSlotById(Long slotId) {
        return doctorSlotRepository.findById(slotId);
    }
    
    public DoctorSlot updateSlotStatus(Long slotId, DoctorSlot.SlotStatus newStatus) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Doctor slot not found"));
        slot.setStatus(newStatus);
        slot.setUpdatedBy(1L); // Current user ID
        return doctorSlotRepository.save(slot);
    }
    
    public DoctorSlot unbookSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.BOOKED) {
            throw new RuntimeException("Slot is not booked");
        }
        
        slot.setStatus(DoctorSlot.SlotStatus.AVAILABLE);
        slot.setUpdatedBy(1L); // Current user ID
        
        return doctorSlotRepository.save(slot);
    }
    
    public DoctorSlot blockSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot is not available for blocking");
        }
        
        slot.setStatus(DoctorSlot.SlotStatus.CANCELLED); // Using CANCELLED as blocked
        slot.setUpdatedBy(1L); // Current user ID
        
        return doctorSlotRepository.save(slot);
    }
    
    public DoctorSlot unblockSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.CANCELLED) {
            throw new RuntimeException("Slot is not blocked");
        }
        
        slot.setStatus(DoctorSlot.SlotStatus.AVAILABLE);
        slot.setUpdatedBy(1L); // Current user ID
        
        return doctorSlotRepository.save(slot);
    }
    
    public void deleteSlot(Long slotId) {
        DoctorSlot slot = doctorSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        doctorSlotRepository.delete(slot);
    }
    
    public Map<String, Object> getSlotStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalSlots = doctorSlotRepository.count();
        long availableSlots = doctorSlotRepository.findAvailableSlotsByDate(LocalDate.now()).size();
        long bookedSlots = doctorSlotRepository.findByDoctorDoctorIdAndSlotDateAndStatus(1L, LocalDate.now(), DoctorSlot.SlotStatus.BOOKED).size();
        long cancelledSlots = doctorSlotRepository.findByDoctorDoctorIdAndSlotDateAndStatus(1L, LocalDate.now(), DoctorSlot.SlotStatus.CANCELLED).size();
        
        stats.put("totalSlots", totalSlots);
        stats.put("availableSlots", availableSlots);
        stats.put("bookedSlots", bookedSlots);
        stats.put("cancelledSlots", cancelledSlots);
        
        return stats;
    }
    
    public List<DoctorSlot> getAllSlots() {
        return doctorSlotRepository.findAll();
    }
}