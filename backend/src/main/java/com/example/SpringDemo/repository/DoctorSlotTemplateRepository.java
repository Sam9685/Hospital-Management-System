package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.DoctorSlotTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorSlotTemplateRepository extends JpaRepository<DoctorSlotTemplate, Long> {
    
    List<DoctorSlotTemplate> findByDoctorDoctorIdAndIsActiveTrue(Long doctorId);
    
    List<DoctorSlotTemplate> findByDoctorDoctorIdAndDayOfWeekAndIsActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
    
    @Query("SELECT t FROM DoctorSlotTemplate t WHERE t.doctor.doctorId = :doctorId AND t.dayOfWeek = :dayOfWeek AND t.isActive = true ORDER BY t.startTime")
    List<DoctorSlotTemplate> findActiveTemplatesByDoctorAndDay(@Param("doctorId") Long doctorId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}