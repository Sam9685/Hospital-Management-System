package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Complaint;
import com.example.SpringDemo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    @Query("SELECT c FROM Complaint c WHERE c.patient.id = :patientId AND c.deletedAt IS NULL")
    Page<Complaint> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE c.assignedTo.id = :assignedToId AND c.deletedAt IS NULL")
    Page<Complaint> findByAssignedTo(@Param("assignedToId") Long assignedToId, Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:category IS NULL OR c.category = :categoryEnum) AND " +
           "(:status IS NULL OR c.status = :statusEnum) AND " +
           "(:priority IS NULL OR c.priority = :priorityEnum) AND " +
           "(:patientId IS NULL OR c.patient.id = :patientId) AND " +
           "(:assignedToId IS NULL OR c.assignedTo.id = :assignedToId) AND " +
           "c.deletedAt IS NULL")
    Page<Complaint> findComplaintsWithFilters(@Param("category") String category,
                                             @Param("categoryEnum") Complaint.Category categoryEnum,
                                             @Param("status") String status,
                                             @Param("statusEnum") Complaint.Status statusEnum,
                                             @Param("priority") String priority,
                                             @Param("priorityEnum") Complaint.Priority priorityEnum,
                                             @Param("patientId") Long patientId,
                                             @Param("assignedToId") Long assignedToId,
                                             Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:patientId IS NULL OR c.patient.id = :patientId) AND " +
           "(:assignedToId IS NULL OR c.assignedTo.id = :assignedToId) AND " +
           "c.deletedAt IS NULL")
    Page<Complaint> searchComplaints(@Param("title") String title,
                                    @Param("description") String description,
                                    @Param("category") String category,
                                    @Param("status") String status,
                                    @Param("priority") String priority,
                                    @Param("patientId") Long patientId,
                                    @Param("assignedToId") Long assignedToId,
                                    Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE c.status = :status AND c.deletedAt IS NULL")
    List<Complaint> findByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status AND c.deletedAt IS NULL")
    Long countByStatusAndDeletedAtIsNull(@Param("status") String status);
    
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.deletedAt IS NULL")
    Long countByDeletedAtIsNull();
    
    @Query("SELECT c FROM Complaint c WHERE c.patient.id = :patientId AND c.deletedAt IS NULL")
    Page<Complaint> findByPatientIdAndDeletedAtIsNull(@Param("patientId") Long patientId, Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE c.patient.id = :patientId AND " +
           "(:status IS NULL OR c.status = :statusEnum) AND " +
           "c.deletedAt IS NULL")
    Page<Complaint> findByPatientIdAndStatusAndDeletedAtIsNull(@Param("patientId") Long patientId, 
                                                               @Param("status") String status,
                                                               @Param("statusEnum") Complaint.Status statusEnum, 
                                                               Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Complaint> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    @Query("SELECT c FROM Complaint c WHERE c.patient = :patient AND c.status IN ('OPEN', 'IN_PROGRESS') AND c.deletedAt IS NULL")
    List<Complaint> findOpenComplaintsByPatient(@Param("patient") User patient);
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:category IS NULL OR c.category = :categoryEnum) AND " +
           "(:status IS NULL OR c.status = :statusEnum) AND " +
           "(:priority IS NULL OR c.priority = :priorityEnum) AND " +
           "c.deletedAt IS NULL")
    Page<Complaint> findComplaintsWithFilters(@Param("title") String title,
                                             @Param("category") String category,
                                             @Param("categoryEnum") Complaint.Category categoryEnum,
                                             @Param("status") String status,
                                             @Param("statusEnum") Complaint.Status statusEnum,
                                             @Param("priority") String priority,
                                             @Param("priorityEnum") Complaint.Priority priorityEnum,
                                             Pageable pageable);
    
    // Method to include deleted records for display purposes
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:patientId IS NULL OR c.patient.id = :patientId) AND " +
           "(:assignedToId IS NULL OR c.assignedTo.id = :assignedToId)")
    Page<Complaint> findComplaintsWithFiltersIncludingDeleted(@Param("title") String title,
                                                            @Param("description") String description,
                                                            @Param("category") String category,
                                                            @Param("status") String status,
                                                            @Param("priority") String priority,
                                                            @Param("patientId") Long patientId,
                                                            @Param("assignedToId") Long assignedToId,
                                                            Pageable pageable);
    
    // Method to get available complaints (unassigned or assigned to specific admin)
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:patientId IS NULL OR c.patient.id = :patientId) AND " +
           "c.deletedAt IS NULL AND " +
           "(c.assignedTo IS NULL OR c.assignedTo.id = :assignedToId)")
    Page<Complaint> findAvailableComplaints(@Param("title") String title,
                                           @Param("description") String description,
                                           @Param("category") String category,
                                           @Param("status") String status,
                                           @Param("priority") String priority,
                                           @Param("patientId") Long patientId,
                                           @Param("assignedToId") Long assignedToId,
                                           Pageable pageable);
}
