package com.example.SpringDemo.dto;

import com.example.SpringDemo.util.CustomLocalTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PaymentWithAppointmentRequest {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String method;
    
    private String status;
    
    private String transactionId;
    
    // Card payment fields
    private String cardholderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String billingAddress;
    
    // UPI payment fields
    private String upiId;
    private String mobileNumber;
    
    // Appointment data fields
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
    
    @NotNull(message = "Appointment type is required")
    private String appointmentType;
    
    @NotBlank(message = "Symptoms are required")
    private String symptoms;
    
    private String notes;
    
    @NotNull(message = "Slot ID is required")
    private Long slotId;
}
