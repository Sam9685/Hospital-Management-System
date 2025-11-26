package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.PaymentRequest;
import com.example.SpringDemo.dto.PaymentWithAppointmentRequest;
import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.Payment;
import com.example.SpringDemo.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(request);
            return ResponseEntity.ok(ApiResponse.success("Payment created successfully", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/with-appointment")
    public ResponseEntity<ApiResponse<Payment>> createPaymentWithAppointment(@Valid @RequestBody PaymentWithAppointmentRequest request) {
        try {
            Payment payment = paymentService.createPaymentWithAppointment(request);
            return ResponseEntity.ok(ApiResponse.success("Payment created successfully. Complete payment to confirm appointment.", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<Appointment>> confirmPaymentAndCreateAppointment(@PathVariable String paymentId) {
        try {
            Appointment appointment = paymentService.confirmPaymentAndCreateAppointment(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Payment successful and appointment scheduled", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{paymentId}/link-appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<Payment>> linkPaymentToAppointment(
            @PathVariable String paymentId, 
            @PathVariable Long appointmentId) {
        try {
            Payment payment = paymentService.linkPaymentToAppointment(paymentId, appointmentId);
            return ResponseEntity.ok(ApiResponse.success("Payment linked to appointment successfully", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Payment>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long appointmentId) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Payment> payments = paymentService.getAllPayments(
            status, method, patientId, appointmentId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Page<Payment>>> getPaymentsByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Payment> payments = paymentService.getPaymentsByPatient(patientId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByAppointment(@PathVariable Long appointmentId) {
        List<Payment> payments = paymentService.getPaymentsByAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Payment>>> searchPayments(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Payment> payments = paymentService.searchPayments(
            paymentId, transactionId, status, method, patientId, appointmentId, 
            minAmount, maxAmount, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        if (payment.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(payment.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment not found"));
        }
    }
    
    @GetMapping("/payment-id/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByPaymentId(@PathVariable String paymentId) {
        Optional<Payment> payment = paymentService.getPaymentByPaymentId(paymentId);
        if (payment.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(payment.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment not found"));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Payment>> updatePayment(@PathVariable Long id, 
                                                             @Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.updatePayment(id, request);
            return ResponseEntity.ok(ApiResponse.success("Payment updated successfully", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Payment>> updatePaymentStatus(
            @PathVariable Long id, 
            @RequestParam String status,
            @RequestParam(required = false) String transactionId) {
        try {
            Payment payment = paymentService.updatePaymentStatus(id, status, transactionId);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getPaymentStats() {
        try {
            Object stats = paymentService.getPaymentStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getRevenueStats(
            @RequestParam(required = false) String period) {
        try {
            Object stats = paymentService.getRevenueStats(period);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
