package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    @Query("SELECT d FROM Doctor d WHERE d.email = :email AND d.deletedAt IS NULL")
    Optional<Doctor> findByEmail(@Param("email") String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization AND d.active = true AND d.deletedAt IS NULL")
    List<Doctor> findBySpecializationAndActive(@Param("specialization") Specialization specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization.id = :specializationId AND d.active = true AND d.deletedAt IS NULL")
    List<Doctor> findBySpecializationIdAndActive(@Param("specializationId") Long specializationId);
    
    @Query("SELECT d FROM Doctor d WHERE d.active = true AND d.deletedAt IS NULL")
    List<Doctor> findAllActive();
    
    
    @Query("SELECT d FROM Doctor d WHERE d.active = :active AND d.deletedAt IS NULL")
    List<Doctor> findByActiveAndDeletedAtIsNull(@Param("active") Boolean active);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.active = :active AND d.deletedAt IS NULL")
    Long countByActiveAndDeletedAtIsNull(@Param("active") Boolean active);
    
    @Query("SELECT d FROM Doctor d WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:specializationId IS NULL OR d.specialization.id = :specializationId) AND " +
           "(:minExperience IS NULL OR d.yearsOfExp >= :minExperience) AND " +
           "(:maxExperience IS NULL OR d.yearsOfExp <= :maxExperience) AND " +
           "(:minFee IS NULL OR d.consultationFee >= :minFee) AND " +
           "(:maxFee IS NULL OR d.consultationFee <= :maxFee) AND " +
           "d.active = true AND d.deletedAt IS NULL")
    Page<Doctor> findDoctorsWithFilters(@Param("name") String name,
                                       @Param("specializationId") Long specializationId,
                                       @Param("minExperience") Integer minExperience,
                                       @Param("maxExperience") Integer maxExperience,
                                       @Param("minFee") BigDecimal minFee,
                                       @Param("maxFee") BigDecimal maxFee,
                                       Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE d.licenseNumber = :licenseNumber AND d.deletedAt IS NULL")
    Optional<Doctor> findByLicenseNumber(@Param("licenseNumber") String licenseNumber);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    
    @Query("SELECT d FROM Doctor d JOIN d.specialization s WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(d.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:specialization IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:active IS NULL OR (:active = 'true' AND d.active = true) OR (:active = 'false' AND d.active = false)) AND " +
           "d.deletedAt IS NULL")
    Page<Doctor> findDoctorsWithFilters(@Param("name") String name,
                                       @Param("email") String email,
                                       @Param("specialization") String specialization,
                                       @Param("active") String active,
                                       Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE d.doctorId = :id AND d.deletedAt IS NULL")
    Optional<Doctor> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    // Methods to include deleted records for display purposes
    @Query("SELECT d FROM Doctor d WHERE d.active = true")
    List<Doctor> findAllActiveIncludingDeleted();
    
    @Query("SELECT d FROM Doctor d JOIN d.specialization s WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(d.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:specialization IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:active IS NULL OR (:active = 'true' AND d.active = true) OR (:active = 'false' AND d.active = false))")
    Page<Doctor> findDoctorsWithFiltersIncludingDeleted(@Param("name") String name,
                                                       @Param("email") String email,
                                                       @Param("specialization") String specialization,
                                                       @Param("active") String active,
                                                       Pageable pageable);
    
    @Query("SELECT d FROM Doctor d JOIN d.specialization s WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(d.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:specializationId IS NULL OR s.specializationId = :specializationId) AND " +
           "(:active IS NULL OR (:active = 'true' AND d.active = true) OR (:active = 'false' AND d.active = false))")
    Page<Doctor> findDoctorsWithFiltersBySpecializationIdIncludingDeleted(@Param("name") String name,
                                                                         @Param("email") String email,
                                                                         @Param("specializationId") Long specializationId,
                                                                         @Param("active") String active,
                                                                         Pageable pageable);
}
