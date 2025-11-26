package com.example.SpringDemo.service;

import com.example.SpringDemo.dto.DoctorRequest;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    
    @Autowired
    private SpecializationRepository specializationRepository;
    
    @Autowired
    private DoctorSlotGeneratorService slotGeneratorService;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    
    @Autowired
    private AppointmentService appointmentService;
    
    public Doctor createDoctor(DoctorRequest request) {
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
        
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists");
        }
        
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        Doctor doctor = new Doctor();
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        // Use the provided password
        doctor.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        doctor.setContact(request.getContact());
        doctor.setGender(request.getGender());
        doctor.setEmergencyContactName(request.getEmergencyContactName());
        doctor.setEmergencyContactNum(request.getEmergencyContactNum());
        doctor.setState(request.getState());
        doctor.setCity(request.getCity());
        doctor.setAddress(request.getAddress());
        doctor.setCountry(request.getCountry());
        doctor.setCountryCode(request.getCountryCode());
        doctor.setPostalCode(request.getPostalCode());
        doctor.setBloodGroup(request.getBloodGroup());
        doctor.setProfileUrl(request.getProfileUrl());
        doctor.setSpecialization(specialization);
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setYearsOfExp(request.getYearsOfExp());
        doctor.setQualification(request.getQualification());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setJoiningDate(request.getJoiningDate());
        doctor.setBio(request.getBio());
        doctor.setActive(request.getActive() != null ? request.getActive() : true);
        
        // Set slot management fields
        doctor.setSlotStartTime(request.getSlotStartTime());
        doctor.setSlotEndTime(request.getSlotEndTime());
        doctor.setAppointmentDuration(request.getAppointmentDuration());
        doctor.setWorkingDays(request.getWorkingDays());
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Generate slots for the next month
        slotGeneratorService.generateSlotsForDoctor(savedDoctor);
        
        return savedDoctor;
    }
    
    public Page<Doctor> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }
    
    public Page<Doctor> getAllDoctors(String name, String email, Long specialization, String active, Pageable pageable) {
        // Use the method that includes deleted records for display purposes with specialization ID
        return doctorRepository.findDoctorsWithFiltersBySpecializationIdIncludingDeleted(name, email, specialization, active, pageable);
    }
    
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findAllActive();
    }
    
    public List<Specialization> getSpecializations() {
        return specializationRepository.findAll();
    }
    
    public List<Doctor> getDoctorsBySpecialization(Long specializationId) {
        return doctorRepository.findBySpecializationIdAndActive(specializationId);
    }
    
    public Page<Doctor> searchDoctors(String name, Long specializationId, Integer minExperience, 
                                     Integer maxExperience, BigDecimal minFee, BigDecimal maxFee, 
                                     Pageable pageable) {
        return doctorRepository.findDoctorsWithFilters(name, specializationId, minExperience, 
                                                      maxExperience, minFee, maxFee, pageable);
    }
    
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
    
    public Doctor updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
        
        // Check if email is being changed and if it already exists
        if (!doctor.getEmail().equals(request.getEmail()) && doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if license number is being changed and if it already exists
        if (!doctor.getLicenseNumber().equals(request.getLicenseNumber()) && doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists");
        }
        
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setContact(request.getContact());
        doctor.setGender(request.getGender());
        doctor.setEmergencyContactName(request.getEmergencyContactName());
        doctor.setEmergencyContactNum(request.getEmergencyContactNum());
        doctor.setState(request.getState());
        doctor.setCity(request.getCity());
        doctor.setAddress(request.getAddress());
        doctor.setCountry(request.getCountry());
        doctor.setCountryCode(request.getCountryCode());
        doctor.setPostalCode(request.getPostalCode());
        doctor.setBloodGroup(request.getBloodGroup());
        doctor.setProfileUrl(request.getProfileUrl());
        doctor.setSpecialization(specialization);
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setYearsOfExp(request.getYearsOfExp());
        doctor.setQualification(request.getQualification());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBio(request.getBio());
        if (request.getActive() != null) {
            doctor.setActive(request.getActive());
        }
        
        // Update slot management fields
        doctor.setSlotStartTime(request.getSlotStartTime());
        doctor.setSlotEndTime(request.getSlotEndTime());
        doctor.setAppointmentDuration(request.getAppointmentDuration());
        doctor.setWorkingDays(request.getWorkingDays());
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Handle slot configuration changes for existing doctor
        slotGeneratorService.handleWorkingDaysUpdate(savedDoctor);
        
        return savedDoctor;
    }
    
    public Map<String, Object> deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Cancel all scheduled appointments for this doctor
        int cancelledAppointments = appointmentService.cancelAllAppointmentsForDoctor(id, "Doctor is no longer available");
        
        // Mark doctor as deleted
        doctor.setDeletedAt(java.time.LocalDateTime.now());
        doctor.setDeletedBy(doctor.getDoctorId());
        doctor.setActive(false);
        doctorRepository.save(doctor);
        
        // Return result with cancellation info
        Map<String, Object> result = new HashMap<>();
        result.put("doctorId", id);
        result.put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());
        result.put("cancelledAppointments", cancelledAppointments);
        result.put("message", "Doctor deleted successfully and " + cancelledAppointments + " appointments cancelled");
        
        return result;
    }
    
    
    public Doctor updateDoctor(Long id, Map<String, Object> updateData) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Only update fields that are provided and not null
        if (updateData.containsKey("firstName") && updateData.get("firstName") != null) {
            doctor.setFirstName((String) updateData.get("firstName"));
        }
        if (updateData.containsKey("lastName") && updateData.get("lastName") != null) {
            doctor.setLastName((String) updateData.get("lastName"));
        }
        if (updateData.containsKey("email") && updateData.get("email") != null) {
            String newEmail = (String) updateData.get("email");
            if (!doctor.getEmail().equals(newEmail) && doctorRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already exists");
            }
            doctor.setEmail(newEmail);
        }
        if (updateData.containsKey("contact") && updateData.get("contact") != null) {
            doctor.setContact((String) updateData.get("contact"));
        }
        if (updateData.containsKey("gender") && updateData.get("gender") != null) {
            String genderStr = updateData.get("gender").toString();
            try {
                doctor.setGender(Doctor.Gender.valueOf(genderStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid gender: " + genderStr);
            }
        }
        if (updateData.containsKey("emergencyContactName") && updateData.get("emergencyContactName") != null) {
            doctor.setEmergencyContactName((String) updateData.get("emergencyContactName"));
        }
        if (updateData.containsKey("emergencyContactNum") && updateData.get("emergencyContactNum") != null) {
            doctor.setEmergencyContactNum((String) updateData.get("emergencyContactNum"));
        }
        if (updateData.containsKey("state") && updateData.get("state") != null) {
            doctor.setState((String) updateData.get("state"));
        }
        if (updateData.containsKey("city") && updateData.get("city") != null) {
            doctor.setCity((String) updateData.get("city"));
        }
        if (updateData.containsKey("address") && updateData.get("address") != null) {
            doctor.setAddress((String) updateData.get("address"));
        }
        if (updateData.containsKey("country") && updateData.get("country") != null) {
            doctor.setCountry((String) updateData.get("country"));
        }
        if (updateData.containsKey("countryCode") && updateData.get("countryCode") != null) {
            doctor.setCountryCode((String) updateData.get("countryCode"));
        }
        if (updateData.containsKey("postalCode") && updateData.get("postalCode") != null) {
            doctor.setPostalCode((String) updateData.get("postalCode"));
        }
        if (updateData.containsKey("bloodGroup") && updateData.get("bloodGroup") != null) {
            doctor.setBloodGroup((String) updateData.get("bloodGroup"));
        }
        if (updateData.containsKey("profileUrl") && updateData.get("profileUrl") != null) {
            doctor.setProfileUrl((String) updateData.get("profileUrl"));
        }
        if (updateData.containsKey("licenseNumber") && updateData.get("licenseNumber") != null) {
            String newLicenseNumber = (String) updateData.get("licenseNumber");
            if (!doctor.getLicenseNumber().equals(newLicenseNumber) && doctorRepository.existsByLicenseNumber(newLicenseNumber)) {
                throw new RuntimeException("License number already exists");
            }
            doctor.setLicenseNumber(newLicenseNumber);
        }
        if (updateData.containsKey("yearsOfExp") && updateData.get("yearsOfExp") != null) {
            doctor.setYearsOfExp((Integer) updateData.get("yearsOfExp"));
        }
        if (updateData.containsKey("qualification") && updateData.get("qualification") != null) {
            doctor.setQualification((String) updateData.get("qualification"));
        }
        if (updateData.containsKey("consultationFee") && updateData.get("consultationFee") != null) {
            Object fee = updateData.get("consultationFee");
            if (fee instanceof Number) {
                doctor.setConsultationFee(java.math.BigDecimal.valueOf(((Number) fee).doubleValue()));
            } else if (fee instanceof String) {
                doctor.setConsultationFee(new java.math.BigDecimal((String) fee));
            }
        }
        if (updateData.containsKey("bio") && updateData.get("bio") != null) {
            doctor.setBio((String) updateData.get("bio"));
        }
        if (updateData.containsKey("active") && updateData.get("active") != null) {
            Boolean newActiveStatus = (Boolean) updateData.get("active");
            doctor.setActive(newActiveStatus);
            
            // Note: Slots are not deleted when doctor becomes inactive
            // They are only filtered based on active status in the queries
            System.out.println("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " active status changed to: " + newActiveStatus);
        }
        
        // Handle slot-related updates
        boolean slotConfigChanged = false;
        if (updateData.containsKey("slotStartTime") && updateData.get("slotStartTime") != null) {
            doctor.setSlotStartTime((String) updateData.get("slotStartTime"));
            slotConfigChanged = true;
        }
        if (updateData.containsKey("slotEndTime") && updateData.get("slotEndTime") != null) {
            doctor.setSlotEndTime((String) updateData.get("slotEndTime"));
            slotConfigChanged = true;
        }
        if (updateData.containsKey("appointmentDuration") && updateData.get("appointmentDuration") != null) {
            doctor.setAppointmentDuration((Integer) updateData.get("appointmentDuration"));
            slotConfigChanged = true;
        }
        if (updateData.containsKey("workingDays") && updateData.get("workingDays") != null) {
            doctor.setWorkingDays((String) updateData.get("workingDays"));
            slotConfigChanged = true;
        }
        
        // Update audit fields
        doctor.setUpdatedAt(java.time.LocalDateTime.now());
        doctor.setUpdatedBy(doctor.getDoctorId());
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Handle slot configuration changes
        if (slotConfigChanged) {
            slotGeneratorService.handleWorkingDaysUpdate(savedDoctor);
        }
        
        return savedDoctor;
    }
    
    public Object getDoctorStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDoctors", doctorRepository.count());
        stats.put("activeDoctors", doctorRepository.countByActiveAndDeletedAtIsNull(true));
        stats.put("inactiveDoctors", doctorRepository.countByActiveAndDeletedAtIsNull(false));
        
        return stats;
    }
}
