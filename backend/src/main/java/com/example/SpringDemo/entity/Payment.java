package com.example.SpringDemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Method method;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    // Card payment fields
    @Column(name = "cardholder_name")
    private String cardholderName;
    
    @Column(name = "card_number")
    private String cardNumber;
    
    @Column(name = "expiry_date")
    private String expiryDate;
    
    @Column(name = "cvv")
    private String cvv;
    
    @Column(name = "billing_address")
    private String billingAddress;
    
    // UPI payment fields
    @Column(name = "upi_id")
    private String upiId;
    
    @Column(name = "mobile_number")
    private String mobileNumber;
    
    // Temporary appointment data fields (for payment-first flow)
    @Column(name = "temp_doctor_id")
    private Long tempDoctorId;
    
    @Column(name = "temp_appointment_date")
    private java.time.LocalDate tempAppointmentDate;
    
    @Column(name = "temp_appointment_time")
    private java.time.LocalTime tempAppointmentTime;
    
    @Column(name = "temp_end_time")
    private java.time.LocalTime tempEndTime;
    
    @Column(name = "temp_appointment_type")
    private String tempAppointmentType;
    
    @Column(name = "temp_symptoms")
    private String tempSymptoms;
    
    @Column(name = "temp_notes")
    private String tempNotes;
    
    @Column(name = "temp_slot_id")
    private Long tempSlotId;
    
    // Audit fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    public enum Method {
        CARD, CASH, UPI, NETBANKING
    }
    
    public enum Status {
        SUCCESS, PENDING, FAILED
    }
}
