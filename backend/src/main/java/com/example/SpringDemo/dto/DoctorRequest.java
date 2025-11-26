package com.example.SpringDemo.dto;

import com.example.SpringDemo.entity.Doctor;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DoctorRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 25, message = "First name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 25, message = "Last name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Pattern(regexp = ".*@doctor\\.com$", message = "Doctor email must end with @doctor.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be a valid 10-digit Indian mobile number")
    private String contact;
    
    private Doctor.Gender gender;
    
    @Size(max = 50, message = "Emergency contact name must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Emergency contact name must contain only letters and spaces")
    private String emergencyContactName;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact number must be a valid 10-digit Indian mobile number")
    private String emergencyContactNum;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "State must contain only letters and spaces")
    private String state;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "City must contain only letters and spaces")
    private String city;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Country must contain only letters and spaces")
    private String country;
    
    @Pattern(regexp = "^\\+\\d{1,3}$", message = "Country code must be valid (e.g., +91, +1)")
    private String countryCode;
    
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be exactly 6 digits")
    private String postalCode;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$|^$", message = "Blood group must be valid (e.g., A+, B-, AB+, O-) or empty")
    private String bloodGroup;
    
    @Size(max = 500, message = "Profile URL must not exceed 500 characters")
    private String profileUrl;
    
    @NotNull(message = "Specialization ID is required")
    private Long specializationId;
    
    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z]{2,5}\\d{3,6}$", message = "License number must be 2-5 letters followed by 3-6 digits (e.g., ORTHO002, CARD001)")
    private String licenseNumber;
    
    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExp;
    
    @NotBlank(message = "Qualification is required")
    @Size(min = 2, max = 100, message = "Qualification must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,-]+$", message = "Qualification must contain only letters, numbers, spaces, and common punctuation")
    private String qualification;
    
    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "100.0", inclusive = true, message = "Consultation fee must be at least ₹100")
    @DecimalMax(value = "50000.0", message = "Consultation fee cannot exceed ₹50,000")
    private BigDecimal consultationFee;
    
    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    // Slot management fields
    @NotBlank(message = "Slot start time is required")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Slot start time must be in HH:MM format (e.g., 09:00)")
    private String slotStartTime; // e.g., "09:00"
    
    @NotBlank(message = "Slot end time is required")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Slot end time must be in HH:MM format (e.g., 17:00)")
    private String slotEndTime; // e.g., "17:00"
    
    @NotNull(message = "Appointment duration is required")
    @Min(value = 15, message = "Appointment duration must be at least 15 minutes")
    @Max(value = 120, message = "Appointment duration cannot exceed 120 minutes")
    private Integer appointmentDuration; // in minutes: 15, 30, 45, 60
    
    @NotBlank(message = "Working days are required")
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)(,(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY))*$", 
             message = "Working days must be comma-separated valid day names")
    private String workingDays; // e.g., "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY"
    
    private Boolean active = true;
}
