package com.example.SpringDemo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String method;
    
    private String status;
    
    private String transactionId;
    
    private String cardholderName;
    
    private String cardNumber;
    
    private String expiryDate;
    
    private String cvv;
    
    private String billingAddress;
    
    // UPI fields
    private String upiId;
    
    private String mobileNumber;
}
