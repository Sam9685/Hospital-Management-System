package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.deletedAt IS NULL")
    List<Session> findByUserIdAndDeletedAtIsNull(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isActive = true AND s.deletedAt IS NULL")
    List<Session> findByUserIdAndIsActiveTrueAndDeletedAtIsNull(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Session s WHERE s.doctorId = :doctorId AND s.isActive = true AND s.deletedAt IS NULL")
    List<Session> findByDoctorIdAndIsActiveTrueAndDeletedAtIsNull(@Param("doctorId") Long doctorId);
    
    @Query("SELECT s FROM Session s WHERE s.isActive = true AND s.deletedAt IS NULL")
    List<Session> findByIsActiveTrueAndDeletedAtIsNull();
    
    @Query("SELECT s FROM Session s WHERE s.isActive = false AND s.deletedAt IS NULL")
    List<Session> findByIsActiveFalseAndDeletedAtIsNull();
    
    @Query("SELECT s FROM Session s WHERE " +
           "(:userId IS NULL OR s.user.id = :userId) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive) AND " +
           "s.deletedAt IS NULL")
    Page<Session> findSessionsWithFilters(@Param("userId") Long userId,
                                         @Param("isActive") Boolean isActive,
                                         Pageable pageable);
    
    @Query("SELECT s FROM Session s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Session> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.deletedAt IS NULL")
    Long countByDeletedAtIsNull();
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.isActive = true AND s.deletedAt IS NULL")
    Long countByIsActiveTrueAndDeletedAtIsNull();
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.isActive = false AND s.deletedAt IS NULL")
    Long countByIsActiveFalseAndDeletedAtIsNull();
    
    @Query("SELECT s FROM Session s WHERE s.sessionToken = :sessionToken AND s.isActive = true AND s.deletedAt IS NULL")
    Optional<Session> findBySessionTokenAndIsActiveTrueAndDeletedAtIsNull(@Param("sessionToken") String sessionToken);
    
    @Query("SELECT s FROM Session s WHERE s.expiresAt < :currentTime AND s.isActive = true AND s.deletedAt IS NULL")
    List<Session> findExpiredActiveSessions(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT s FROM Session s WHERE s.isActive = false AND s.updatedAt < :cutoffDate AND s.deletedAt IS NULL")
    List<Session> findByIsActiveFalseAndUpdatedAtBeforeAndDeletedAtIsNull(@Param("cutoffDate") LocalDateTime cutoffDate);
}
