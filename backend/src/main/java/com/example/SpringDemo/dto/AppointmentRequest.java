package com.example.SpringDemo.dto;

import com.example.SpringDemo.util.CustomLocalTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Appointment time is required")
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime appointmentTime;
    
    @NotNull(message = "End time is required")
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime endTime;
    
    @NotBlank(message = "Appointment type is required")
    @Pattern(regexp = "^(CONSULTATION|FOLLOW_UP)$", message = "Appointment type must be either CONSULTATION or FOLLOW_UP")
    private String appointmentType;
    
    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "100.0", inclusive = true, message = "Consultation fee must be at least ₹100")
    @DecimalMax(value = "50000.0", message = "Consultation fee cannot exceed ₹50,000")
    private BigDecimal consultationFee;
    
    @Size(max = 1000, message = "Symptoms must not exceed 1000 characters")
    private String symptoms;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
