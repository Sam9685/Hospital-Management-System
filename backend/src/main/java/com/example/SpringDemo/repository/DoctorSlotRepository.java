package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Long> {
    
    List<DoctorSlot> findByDoctorDoctorIdAndSlotDateAndStatus(Long doctorId, LocalDate slotDate, DoctorSlot.SlotStatus status);
    
    List<DoctorSlot> findByDoctorDoctorIdAndSlotDateOrderByStartTime(Long doctorId, LocalDate slotDate);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.doctor.doctorId = :doctorId AND s.slotDate = :slotDate AND s.status = 'AVAILABLE' AND s.doctor.active = true AND s.doctor.deletedAt IS NULL ORDER BY s.startTime")
    List<DoctorSlot> findAvailableSlotsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("slotDate") LocalDate slotDate);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.doctor.doctorId = :doctorId AND s.slotDate >= :startDate AND s.slotDate <= :endDate ORDER BY s.slotDate, s.startTime")
    List<DoctorSlot> findSlotsByDoctorAndDateRange(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.slotDate = :slotDate AND s.status = 'AVAILABLE' AND s.doctor.active = true AND s.doctor.deletedAt IS NULL ORDER BY s.doctor.doctorId, s.startTime")
    List<DoctorSlot> findAvailableSlotsByDate(@Param("slotDate") LocalDate slotDate);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.slotDate = :slotDate AND s.doctor.specialization.specializationId = :specializationId AND s.status = 'AVAILABLE' AND s.doctor.active = true AND s.doctor.deletedAt IS NULL ORDER BY s.doctor.doctorId, s.startTime")
    List<DoctorSlot> findAvailableSlotsByDateAndSpecialization(@Param("slotDate") LocalDate slotDate, @Param("specializationId") Long specializationId);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.slotDate = :slotDate AND s.status = 'AVAILABLE' AND s.doctor.active = true AND s.doctor.deletedAt IS NULL AND (:currentTime IS NULL OR s.startTime > :currentTime) ORDER BY s.doctor.doctorId, s.startTime")
    List<DoctorSlot> findAvailableSlotsByDateWithTimeFilter(@Param("slotDate") LocalDate slotDate, @Param("currentTime") LocalTime currentTime);
    
    @Query("SELECT s FROM DoctorSlot s WHERE s.slotDate = :slotDate AND s.doctor.specialization.specializationId = :specializationId AND s.status = 'AVAILABLE' AND s.doctor.active = true AND s.doctor.deletedAt IS NULL AND (:currentTime IS NULL OR s.startTime > :currentTime) ORDER BY s.doctor.doctorId, s.startTime")
    List<DoctorSlot> findAvailableSlotsByDateAndSpecializationWithTimeFilter(@Param("slotDate") LocalDate slotDate, @Param("specializationId") Long specializationId, @Param("currentTime") LocalTime currentTime);
    
    boolean existsByDoctorDoctorIdAndSlotDateAndStartTimeAndStatus(Long doctorId, LocalDate slotDate, LocalTime startTime, DoctorSlot.SlotStatus status);
    
    @Query("SELECT COUNT(s) > 0 FROM DoctorSlot s WHERE s.doctor = :doctor AND s.slotDate = :slotDate")
    boolean existsByDoctorAndSlotDate(@Param("doctor") com.example.SpringDemo.entity.Doctor doctor, @Param("slotDate") LocalDate slotDate);
}