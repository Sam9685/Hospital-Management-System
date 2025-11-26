package com.example.SpringDemo.service;

import com.example.SpringDemo.dto.PaymentRequest;
import com.example.SpringDemo.dto.PaymentWithAppointmentRequest;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.DoctorSlot;
import com.example.SpringDemo.entity.Payment;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.repository.AppointmentRepository;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.repository.DoctorSlotRepository;
import com.example.SpringDemo.repository.PaymentRepository;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DoctorSlotRepository doctorSlotRepository;
    
    public Payment createPayment(PaymentRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setAppointment(appointment);
        payment.setPatient(patient);
        payment.setAmount(request.getAmount());
        payment.setMethod(Payment.Method.valueOf(request.getMethod().toUpperCase()));
        payment.setStatus(request.getStatus() != null ? Payment.Status.valueOf(request.getStatus().toUpperCase()) : Payment.Status.PENDING);
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(patient.getId());
        
        // Set payment method specific fields
        if (request.getCardholderName() != null) {
            payment.setCardholderName(request.getCardholderName());
        }
        if (request.getCardNumber() != null) {
            payment.setCardNumber(request.getCardNumber());
        }
        if (request.getExpiryDate() != null) {
            payment.setExpiryDate(request.getExpiryDate());
        }
        if (request.getCvv() != null) {
            payment.setCvv(request.getCvv());
        }
        if (request.getBillingAddress() != null) {
            payment.setBillingAddress(request.getBillingAddress());
        }
        if (request.getUpiId() != null) {
            payment.setUpiId(request.getUpiId());
        }
        if (request.getMobileNumber() != null) {
            payment.setMobileNumber(request.getMobileNumber());
        }
        
        // Generate transaction ID if not provided
        if (payment.getTransactionId() == null) {
            payment.setTransactionId("TXN" + System.currentTimeMillis());
        }
        
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Payment createPaymentWithAppointment(PaymentWithAppointmentRequest request) {
        // Validate patient exists
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // Validate doctor exists
        doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Validate slot exists and is available
        DoctorSlot slot = doctorSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Doctor slot not found"));
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.AVAILABLE) {
            throw new RuntimeException("Selected slot is no longer available");
        }
        
        // Create payment with temporary appointment data
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setPatient(patient);
        payment.setAmount(request.getAmount());
        payment.setMethod(Payment.Method.valueOf(request.getMethod().toUpperCase()));
        payment.setStatus(Payment.Status.PENDING);
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(patient.getId());
        
        // Store temporary appointment data
        payment.setTempDoctorId(request.getDoctorId());
        payment.setTempAppointmentDate(request.getAppointmentDate());
        payment.setTempAppointmentTime(request.getAppointmentTime());
        payment.setTempEndTime(request.getEndTime());
        payment.setTempAppointmentType(request.getAppointmentType());
        payment.setTempSymptoms(request.getSymptoms());
        payment.setTempNotes(request.getNotes());
        payment.setTempSlotId(request.getSlotId());
        
        // Set payment method specific fields
        if (request.getCardholderName() != null) {
            payment.setCardholderName(request.getCardholderName());
        }
        if (request.getCardNumber() != null) {
            payment.setCardNumber(request.getCardNumber());
        }
        if (request.getExpiryDate() != null) {
            payment.setExpiryDate(request.getExpiryDate());
        }
        if (request.getCvv() != null) {
            payment.setCvv(request.getCvv());
        }
        if (request.getBillingAddress() != null) {
            payment.setBillingAddress(request.getBillingAddress());
        }
        if (request.getUpiId() != null) {
            payment.setUpiId(request.getUpiId());
        }
        if (request.getMobileNumber() != null) {
            payment.setMobileNumber(request.getMobileNumber());
        }
        
        // Generate transaction ID if not provided
        if (payment.getTransactionId() == null) {
            payment.setTransactionId("TXN" + System.currentTimeMillis());
        }
        
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Appointment confirmPaymentAndCreateAppointment(String paymentId) {
        System.out.println("=== CONFIRM PAYMENT AND CREATE APPOINTMENT DEBUG ===");
        System.out.println("Payment ID: " + paymentId);
        
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        System.out.println("Payment found - Status: " + payment.getStatus());
        System.out.println("Temp Doctor ID: " + payment.getTempDoctorId());
        System.out.println("Temp Slot ID: " + payment.getTempSlotId());
        
        // Update payment status to SUCCESS
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        
        System.out.println("Payment status updated to SUCCESS");
        
        if (payment.getAppointment() != null) {
            throw new RuntimeException("Appointment already created for this payment");
        }
        
        // Get the slot and mark it as booked
        DoctorSlot slot = doctorSlotRepository.findById(payment.getTempSlotId())
                .orElseThrow(() -> new RuntimeException("Doctor slot not found"));
        
        System.out.println("Slot found - Status: " + slot.getStatus());
        
        if (slot.getStatus() != DoctorSlot.SlotStatus.AVAILABLE) {
            throw new RuntimeException("Selected slot is no longer available");
        }
        
        System.out.println("Creating appointment...");
        
        // Create the appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(payment.getPatient());
        appointment.setDoctor(doctorRepository.findById(payment.getTempDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found")));
        appointment.setDoctorSlot(slot);
        appointment.setAppointmentDate(payment.getTempAppointmentDate());
        appointment.setAppointmentTime(payment.getTempAppointmentTime());
        appointment.setEndTime(payment.getTempEndTime());
        appointment.setStatus(Appointment.Status.SCHEDULED);
        appointment.setAppointmentType(Appointment.AppointmentType.valueOf(payment.getTempAppointmentType()));
        appointment.setConsultationFee(payment.getAmount());
        appointment.setSymptoms(payment.getTempSymptoms());
        appointment.setNotes(payment.getTempNotes());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setCreatedBy(payment.getPatient().getId());
        
        // Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        System.out.println("Appointment created successfully with ID: " + savedAppointment.getId());
        
        // Update slot status to booked
        slot.setStatus(DoctorSlot.SlotStatus.BOOKED);
        doctorSlotRepository.save(slot);
        System.out.println("Slot status updated to BOOKED");
        
        // Link payment to appointment
        payment.setAppointment(savedAppointment);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        System.out.println("Payment linked to appointment successfully");
        
        return savedAppointment;
    }
    
    public Payment linkPaymentToAppointment(String paymentId, Long appointmentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        payment.setAppointment(appointment);
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public Page<Payment> getAllPayments(String status, String method, Long patientId, 
                                       Long appointmentId, Pageable pageable) {
        return paymentRepository.findPaymentsWithFilters(status, method, patientId, appointmentId, pageable);
    }
    
    public Page<Payment> getPaymentsByPatient(Long patientId, Pageable pageable) {
        return paymentRepository.findByPatientIdAndDeletedAtIsNull(patientId, pageable);
    }
    
    public List<Payment> getPaymentsByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentIdAndDeletedAtIsNull(appointmentId);
    }
    
    public Page<Payment> searchPayments(String paymentId, String transactionId, String status, 
                                      String method, Long patientId, Long appointmentId, 
                                      BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        return paymentRepository.searchPayments(paymentId, transactionId, status, method, 
                                              patientId, appointmentId, minAmount, maxAmount, pageable);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findByIdAndDeletedAtIsNull(id);
    }
    
    public Optional<Payment> getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentIdAndDeletedAtIsNull(paymentId);
    }
    
    public Payment updatePayment(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (request.getAmount() != null) payment.setAmount(request.getAmount());
        if (request.getMethod() != null) payment.setMethod(Payment.Method.valueOf(request.getMethod().toUpperCase()));
        if (request.getStatus() != null) payment.setStatus(Payment.Status.valueOf(request.getStatus().toUpperCase()));
        if (request.getTransactionId() != null) payment.setTransactionId(request.getTransactionId());
        
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setUpdatedBy(payment.getPatient().getId());
        
        return paymentRepository.save(payment);
    }
    
    public Payment updatePaymentStatus(Long id, String status, String transactionId) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(Payment.Status.valueOf(status.toUpperCase()));
        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setDeletedAt(LocalDateTime.now());
        payment.setDeletedBy(payment.getPatient().getId());
        paymentRepository.save(payment);
    }
    
    public Object getPaymentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPayments", paymentRepository.countByDeletedAtIsNull());
        stats.put("successfulPayments", paymentRepository.countByStatusAndDeletedAtIsNull("SUCCESS"));
        stats.put("pendingPayments", paymentRepository.countByStatusAndDeletedAtIsNull("PENDING"));
        stats.put("failedPayments", paymentRepository.countByStatusAndDeletedAtIsNull("FAILED"));
        
        return stats;
    }
    
    public Object getRevenueStats(String period) {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate total revenue
        BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue();
        stats.put("totalRevenue", totalRevenue);
        
        // Calculate revenue by method
        Map<String, BigDecimal> revenueByMethod = paymentRepository.calculateRevenueByMethod();
        stats.put("revenueByMethod", revenueByMethod);
        
        // Calculate daily revenue for the last 30 days
        if ("daily".equals(period)) {
            Map<String, BigDecimal> dailyRevenue = paymentRepository.calculateDailyRevenue();
            stats.put("dailyRevenue", dailyRevenue);
        }
        
        return stats;
    }
}
