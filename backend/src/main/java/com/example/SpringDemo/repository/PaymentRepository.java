package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.id = :appointmentId AND p.deletedAt IS NULL")
    List<Payment> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT p FROM Payment p WHERE p.patient.id = :patientId AND p.deletedAt IS NULL")
    Page<Payment> findByPatientIdAndDeletedAtIsNull(@Param("patientId") Long patientId, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.id = :appointmentId AND p.deletedAt IS NULL")
    List<Payment> findByAppointmentIdAndDeletedAtIsNull(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId AND p.deletedAt IS NULL")
    Optional<Payment> findByPaymentIdAndDeletedAtIsNull(@Param("paymentId") String paymentId);
    
    @Query("SELECT p FROM Payment p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Payment> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.deletedAt IS NULL")
    Long countByDeletedAtIsNull();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.deletedAt IS NULL")
    Long countByStatusAndDeletedAtIsNull(@Param("status") String status);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:method IS NULL OR p.method = :method) AND " +
           "(:patientId IS NULL OR p.patient.id = :patientId) AND " +
           "(:appointmentId IS NULL OR p.appointment.id = :appointmentId) AND " +
           "p.deletedAt IS NULL")
    Page<Payment> findPaymentsWithFilters(@Param("status") String status,
                                         @Param("method") String method,
                                         @Param("patientId") Long patientId,
                                         @Param("appointmentId") Long appointmentId,
                                         Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "(:paymentId IS NULL OR p.paymentId LIKE CONCAT('%', :paymentId, '%')) AND " +
           "(:transactionId IS NULL OR p.transactionId LIKE CONCAT('%', :transactionId, '%')) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:method IS NULL OR p.method = :method) AND " +
           "(:patientId IS NULL OR p.patient.id = :patientId) AND " +
           "(:appointmentId IS NULL OR p.appointment.id = :appointmentId) AND " +
           "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.amount <= :maxAmount) AND " +
           "p.deletedAt IS NULL")
    Page<Payment> searchPayments(@Param("paymentId") String paymentId,
                                @Param("transactionId") String transactionId,
                                @Param("status") String status,
                                @Param("method") String method,
                                @Param("patientId") Long patientId,
                                @Param("appointmentId") Long appointmentId,
                                @Param("minAmount") BigDecimal minAmount,
                                @Param("maxAmount") BigDecimal maxAmount,
                                Pageable pageable);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.deletedAt IS NULL")
    BigDecimal calculateTotalRevenue();
    
    @Query("SELECT p.method, SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.deletedAt IS NULL GROUP BY p.method")
    Map<String, BigDecimal> calculateRevenueByMethod();
    
    @Query("SELECT DATE(p.paymentDate), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDate >= CURRENT_DATE AND p.deletedAt IS NULL GROUP BY DATE(p.paymentDate) ORDER BY DATE(p.paymentDate)")
    Map<String, BigDecimal> calculateDailyRevenue();
}
