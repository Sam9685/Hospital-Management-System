package com.example.SpringDemo.util;

import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.DoctorSlot;
import com.example.SpringDemo.entity.DoctorSlotTemplate;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.repository.AppointmentRepository;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.repository.DoctorSlotRepository;
import com.example.SpringDemo.repository.DoctorSlotTemplateRepository;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataGenerator {

    @Autowired
    private DoctorSlotRepository doctorSlotRepository;

    @Autowired
    private DoctorSlotTemplateRepository doctorSlotTemplateRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    public void generateComprehensiveData() {
        System.out.println("Starting comprehensive data generation...");
        
        // Generate slots for all doctors (1-12) for the next 2 weeks
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(2);
        
        for (long doctorId = 1; doctorId <= 12; doctorId++) {
            try {
                generateSlotsForDoctor(doctorId, startDate, endDate);
                System.out.println("Generated slots for doctor " + doctorId);
            } catch (Exception e) {
                System.err.println("Error generating slots for doctor " + doctorId + ": " + e.getMessage());
            }
        }
        
        System.out.println("Data generation completed!");
    }

    private void generateSlotsForDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        // Get all active templates for this doctor
        List<DoctorSlotTemplate> templates = doctorSlotTemplateRepository.findByDoctorDoctorIdAndIsActiveTrue(doctorId);
        
        if (templates.isEmpty()) {
            System.out.println("No templates found for doctor " + doctorId);
            return;
        }

        List<DoctorSlot> slotsToCreate = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // Find templates for this day of week
            List<DoctorSlotTemplate> dayTemplates = templates.stream()
                .filter(template -> template.getDayOfWeek().equals(dayOfWeek))
                .toList();
            
            for (DoctorSlotTemplate template : dayTemplates) {
                List<DoctorSlot> daySlots = generateSlotsForTemplate(template, currentDate);
                slotsToCreate.addAll(daySlots);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        // Save all generated slots in batches
        if (!slotsToCreate.isEmpty()) {
            doctorSlotRepository.saveAll(slotsToCreate);
            System.out.println("Created " + slotsToCreate.size() + " slots for doctor " + doctorId);
        }
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

    public void generateSampleAppointments() {
        System.out.println("Starting sample appointment generation...");
        
        try {
            // Get some patients and doctors
            List<User> patients = userRepository.findByRole(User.Role.PATIENT);
            List<Doctor> doctors = doctorRepository.findAll();
            
            if (patients.isEmpty() || doctors.isEmpty()) {
                System.out.println("No patients or doctors found. Cannot generate appointments.");
                return;
            }
            
            // Generate past appointments (May-July 2025)
            generatePastAppointments(patients, doctors);
            
            // Generate future appointments (after September 18, 2025)
            generateFutureAppointments(patients, doctors);
            
            System.out.println("Sample appointment generation completed!");
            
        } catch (Exception e) {
            System.err.println("Error generating sample appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePastAppointments(List<User> patients, List<Doctor> doctors) {
        System.out.println("Generating past appointments...");
        
        // May 2025 appointments
        createAppointment(patients.get(0), doctors.get(0), LocalDate.of(2025, 5, 15), 
                         LocalTime.of(9, 0), LocalTime.of(9, 30), 
                         "CONSULTATION", "SCHEDULED", "Chest pain evaluation", 
                         "Patient reported mild chest discomfort during exercise", new BigDecimal("150.00"));
        
        createAppointment(patients.get(1), doctors.get(0), LocalDate.of(2025, 5, 15), 
                         LocalTime.of(10, 0), LocalTime.of(10, 30), 
                         "CONSULTATION", "COMPLETED", "Annual heart checkup", 
                         "Routine cardiovascular examination", new BigDecimal("150.00"));
        
        // June 2025 appointments
        createAppointment(patients.get(2), doctors.get(1), LocalDate.of(2025, 6, 10), 
                         LocalTime.of(10, 0), LocalTime.of(10, 45), 
                         "CONSULTATION", "COMPLETED", "Headache consultation", 
                         "New patient with chronic headaches", new BigDecimal("200.00"));
        
        // July 2025 appointments
        createAppointment(patients.get(0), doctors.get(2), LocalDate.of(2025, 7, 8), 
                         LocalTime.of(8, 0), LocalTime.of(9, 0), 
                         "CONSULTATION", "COMPLETED", "Post-surgery follow-up", 
                         "Gallbladder removal follow-up", new BigDecimal("200.00"));
        
        createAppointment(patients.get(1), doctors.get(3), LocalDate.of(2025, 7, 12), 
                         LocalTime.of(8, 30), LocalTime.of(9, 0), 
                         "CONSULTATION", "COMPLETED", "Heart rhythm evaluation", 
                         "ECG and heart monitoring", new BigDecimal("160.00"));
    }

    private void generateFutureAppointments(List<User> patients, List<Doctor> doctors) {
        System.out.println("Generating future appointments...");
        
        // September 2025 appointments (after 18th)
        createAppointment(patients.get(0), doctors.get(0), LocalDate.of(2025, 9, 20), 
                         LocalTime.of(9, 0), LocalTime.of(9, 30), 
                         "CONSULTATION", "SCHEDULED", "Follow-up consultation", 
                         "Post-treatment follow-up", new BigDecimal("150.00"));
        
        createAppointment(patients.get(1), doctors.get(1), LocalDate.of(2025, 9, 22), 
                         LocalTime.of(10, 0), LocalTime.of(10, 45), 
                         "CONSULTATION", "SCHEDULED", "Migraine follow-up", 
                         "Treatment progress review", new BigDecimal("200.00"));
        
        // October 2025 appointments
        createAppointment(patients.get(2), doctors.get(2), LocalDate.of(2025, 10, 3), 
                         LocalTime.of(8, 0), LocalTime.of(9, 0), 
                         "CONSULTATION", "SCHEDULED", "Knee rehabilitation", 
                         "Post-surgery recovery check", new BigDecimal("180.00"));
        
        createAppointment(patients.get(0), doctors.get(3), LocalDate.of(2025, 10, 8), 
                         LocalTime.of(9, 0), LocalTime.of(9, 30), 
                         "CONSULTATION", "SCHEDULED", "Child development check", 
                         "Growth and development assessment", new BigDecimal("120.00"));
        
        // November 2025 appointments
        createAppointment(patients.get(1), doctors.get(4), LocalDate.of(2025, 11, 5), 
                         LocalTime.of(9, 30), LocalTime.of(10, 0), 
                         "CONSULTATION", "SCHEDULED", "Skin treatment follow-up", 
                         "Acne treatment review", new BigDecimal("170.00"));
        
        createAppointment(patients.get(2), doctors.get(0), LocalDate.of(2025, 11, 20), 
                         LocalTime.of(9, 0), LocalTime.of(9, 30), 
                         "CONSULTATION", "SCHEDULED", "Cardiac stress test", 
                         "Exercise stress test", new BigDecimal("150.00"));
        
        // December 2025 appointments
        createAppointment(patients.get(0), doctors.get(1), LocalDate.of(2025, 12, 3), 
                         LocalTime.of(8, 0), LocalTime.of(9, 0), 
                         "CONSULTATION", "SCHEDULED", "Knee recovery check", 
                         "Post-surgery rehabilitation", new BigDecimal("180.00"));
        
        createAppointment(patients.get(1), doctors.get(2), LocalDate.of(2025, 12, 8), 
                         LocalTime.of(9, 0), LocalTime.of(9, 30), 
                         "CONSULTATION", "SCHEDULED", "Child health check", 
                         "Regular pediatric visit", new BigDecimal("120.00"));
    }

    private void createAppointment(User patient, Doctor doctor, LocalDate date, 
                                 LocalTime startTime, LocalTime endTime, 
                                 String appointmentType, String status, 
                                 String symptoms, String notes, BigDecimal fee) {
        try {
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointment.setAppointmentDate(date);
            appointment.setAppointmentTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setAppointmentType(Appointment.AppointmentType.valueOf(appointmentType));
            appointment.setStatus(Appointment.Status.valueOf(status));
            appointment.setSymptoms(symptoms);
            appointment.setNotes(notes);
            appointment.setConsultationFee(fee);
            appointment.setCreatedBy(1L); // System user
            
            appointmentRepository.save(appointment);
            System.out.println("Created appointment for " + patient.getName() + " with " + doctor.getFullName() + " on " + date);
            
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
        }
    }
}
