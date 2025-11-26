package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Appointment;
import com.example.SpringDemo.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.deletedAt IS NULL")
    Page<Appointment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findByPatientIdAndStatusAndDeletedAtIsNull(@Param("patientId") Long patientId, 
                                                                 @Param("status") String status,
                                                                 @Param("statusEnum") Appointment.Status statusEnum, 
                                                                 Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "(a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime > :currentTime)) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId, 
                                                        @Param("status") String status,
                                                        @Param("statusEnum") Appointment.Status statusEnum,
                                                        @Param("today") java.time.LocalDate today,
                                                        @Param("currentTime") java.time.LocalTime currentTime,
                                                        Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "(a.appointmentDate < :today OR (a.appointmentDate = :today AND a.appointmentTime <= :currentTime)) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findPastAppointmentsByPatient(@Param("patientId") Long patientId, 
                                                    @Param("status") String status,
                                                    @Param("statusEnum") Appointment.Status statusEnum,
                                                    @Param("today") java.time.LocalDate today,
                                                    @Param("currentTime") java.time.LocalTime currentTime,
                                                    Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.deletedAt IS NULL")
    Page<Appointment> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'SCHEDULED' AND a.deletedAt IS NULL")
    List<Appointment> findScheduledAppointmentsByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:fromDate IS NULL OR a.appointmentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR a.appointmentDate <= :toDate) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findMyAppointmentsWithFilters(@Param("doctorId") Long doctorId,
                                                   @Param("status") Appointment.Status status,
                                                   @Param("appointmentType") Appointment.AppointmentType appointmentType,
                                                   @Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate,
                                                   Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:fromDate IS NULL OR a.appointmentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR a.appointmentDate <= :toDate) AND " +
           "(LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.patient.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findMyAppointmentsWithSearch(@Param("doctorId") Long doctorId,
                                                  @Param("status") Appointment.Status status,
                                                  @Param("appointmentType") Appointment.AppointmentType appointmentType,
                                                  @Param("fromDate") LocalDate fromDate,
                                                  @Param("toDate") LocalDate toDate,
                                                  @Param("search") String search,
                                                  Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate = :date AND a.status != 'CANCELLED' AND a.deletedAt IS NULL")
    List<Appointment> findByDoctorAndDate(@Param("doctor") Doctor doctor, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.status != 'CANCELLED' AND a.deletedAt IS NULL")
    List<Appointment> findByDoctorAndDateTime(@Param("doctor") Doctor doctor, 
                                            @Param("date") LocalDate date, 
                                            @Param("time") LocalTime time);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
           "(:fromDate IS NULL OR a.appointmentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR a.appointmentDate <= :toDate) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findAppointmentsWithFilters(@Param("patientId") Long patientId,
                                                 @Param("doctorId") Long doctorId,
                                                 @Param("status") Appointment.Status status,
                                                 @Param("appointmentType") Appointment.AppointmentType appointmentType,
                                                 @Param("fromDate") LocalDate fromDate,
                                                 @Param("toDate") LocalDate toDate,
                                                 Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate = :date AND a.status = 'SCHEDULED' AND a.deletedAt IS NULL")
    Long countScheduledAppointmentsByDoctorAndDate(@Param("doctor") Doctor doctor, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status != 'CANCELLED' AND a.appointmentDate >= :fromDate AND a.deletedAt IS NULL ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId, @Param("fromDate") LocalDate fromDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status != 'CANCELLED' AND a.appointmentDate < :toDate AND a.deletedAt IS NULL ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findPastAppointmentsByPatient(@Param("patientId") Long patientId, @Param("toDate") LocalDate toDate);
    
    // Debug query to get ALL appointments for a patient
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.deletedAt IS NULL ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findAllAppointmentsByPatient(@Param("patientId") Long patientId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status AND a.deletedAt IS NULL")
    Long countByStatus(@Param("status") Appointment.Status status);
    
    @Query("SELECT a FROM Appointment a JOIN a.patient p JOIN a.doctor d WHERE " +
           "(:patientName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
           "(:doctorName IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :doctorName, '%'))) AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentTypeEnum) AND " +
           "(:dateFrom IS NULL OR a.appointmentDate >= :dateFromParsed) AND " +
           "(:dateTo IS NULL OR a.appointmentDate <= :dateToParsed) AND " +
           "a.deletedAt IS NULL")
    Page<Appointment> findAppointmentsWithFilters(@Param("patientName") String patientName,
                                                 @Param("doctorName") String doctorName,
                                                 @Param("status") String status,
                                                 @Param("statusEnum") Appointment.Status statusEnum,
                                                 @Param("appointmentType") String appointmentType,
                                                 @Param("appointmentTypeEnum") Appointment.AppointmentType appointmentTypeEnum,
                                                 @Param("dateFrom") String dateFrom,
                                                 @Param("dateFromParsed") LocalDate dateFromParsed,
                                                 @Param("dateTo") String dateTo,
                                                 @Param("dateToParsed") LocalDate dateToParsed,
                                                 Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Appointment> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    // Method to include deleted records for display purposes
    @Query("SELECT a FROM Appointment a JOIN a.patient p JOIN a.doctor d WHERE " +
           "(:patientName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
           "(:doctorName IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :doctorName, '%'))) AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentTypeEnum) AND " +
           "(:dateFrom IS NULL OR a.appointmentDate >= :dateFromParsed) AND " +
           "(:dateTo IS NULL OR a.appointmentDate <= :dateToParsed)")
    Page<Appointment> findAppointmentsWithFiltersIncludingDeleted(@Param("patientName") String patientName,
                                                                 @Param("doctorName") String doctorName,
                                                                 @Param("status") String status,
                                                                 @Param("statusEnum") Appointment.Status statusEnum,
                                                                 @Param("appointmentType") String appointmentType,
                                                                 @Param("appointmentTypeEnum") Appointment.AppointmentType appointmentTypeEnum,
                                                                 @Param("dateFrom") String dateFrom,
                                                                 @Param("dateFromParsed") LocalDate dateFromParsed,
                                                                 @Param("dateTo") String dateTo,
                                                                 @Param("dateToParsed") LocalDate dateToParsed,
                                                                 Pageable pageable);
    
    // Method with OR logic for patient and doctor name search
    @Query("SELECT a FROM Appointment a JOIN a.patient p JOIN a.doctor d WHERE " +
           "(:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR a.status = :statusEnum) AND " +
           "(:appointmentType IS NULL OR a.appointmentType = :appointmentTypeEnum) AND " +
           "(:dateFrom IS NULL OR a.appointmentDate >= :dateFromParsed) AND " +
           "(:dateTo IS NULL OR a.appointmentDate <= :dateToParsed)")
    Page<Appointment> findAppointmentsWithSearchIncludingDeleted(@Param("searchTerm") String searchTerm,
                                                                @Param("status") String status,
                                                                @Param("statusEnum") Appointment.Status statusEnum,
                                                                @Param("appointmentType") String appointmentType,
                                                                @Param("appointmentTypeEnum") Appointment.AppointmentType appointmentTypeEnum,
                                                                @Param("dateFrom") String dateFrom,
                                                                @Param("dateFromParsed") LocalDate dateFromParsed,
                                                                @Param("dateTo") String dateTo,
                                                                @Param("dateToParsed") LocalDate dateToParsed,
                                                                Pageable pageable);
}
