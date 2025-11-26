package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    
    Optional<Specialization> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT s FROM Specialization s WHERE s.status = 'ACTIVE' AND s.deletedAt IS NULL")
    List<Specialization> findAllActive();
    
    @Query("SELECT s FROM Specialization s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "s.deletedAt IS NULL")
    Page<Specialization> findSpecializationsWithFilters(@Param("name") String name,
                                                       @Param("status") Specialization.Status status,
                                                       Pageable pageable);
}
