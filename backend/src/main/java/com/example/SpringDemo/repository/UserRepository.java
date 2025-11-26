package com.example.SpringDemo.repository;

import com.example.SpringDemo.entity.User;
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
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByContact(String contact);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByContact(String contact);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    List<User> findByRole(@Param("role") User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    Page<User> findByRole(@Param("role") User.Role role, Pageable pageable);
    
    List<User> findByRoleIn(List<User.Role> roles);
    
    @Query("SELECT u FROM User u WHERE " +
           "((:name IS NULL AND :email IS NULL) OR " +
           "(:name IS NOT NULL AND :email IS NULL AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
           "(:name IS NULL AND :email IS NOT NULL AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) OR " +
           "(:name IS NOT NULL AND :email IS NOT NULL AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:gender IS NULL OR u.gender = :gender) AND " +
           "(:status IS NULL OR (:status = 'active' AND u.deletedAt IS NULL) OR (:status = 'inactive' AND u.deletedAt IS NOT NULL))")
    Page<User> findUsersWithFilters(@Param("name") String name, 
                                   @Param("email") String email, 
                                   @Param("role") User.Role role, 
                                   @Param("gender") User.Gender gender,
                                   @Param("status") String status,
                                   Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :fromDate AND u.deletedAt IS NULL")
    List<User> findActiveUsersSince(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findByIdAndDeletedAtIsNull(@Param("id") Long id);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsernameAndDeletedAtIsNull(@Param("username") String username);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL")
    Long countByDeletedAtIsNull();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    Long countByRoleAndDeletedAtIsNull(@Param("role") User.Role role);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:contact IS NULL OR u.contact LIKE CONCAT('%', :contact, '%')) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:gender IS NULL OR u.gender = :gender) AND " +
           "(:city IS NULL OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(u.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "u.deletedAt IS NULL")
    Page<User> searchUsers(@Param("name") String name,
                          @Param("email") String email,
                          @Param("username") String username,
                          @Param("contact") String contact,
                          @Param("role") User.Role role,
                          @Param("gender") User.Gender gender,
                          @Param("city") String city,
                          @Param("state") String state,
                          Pageable pageable);
    
    // Method to include deleted records for display purposes
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:contact IS NULL OR u.contact LIKE CONCAT('%', :contact, '%')) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:gender IS NULL OR u.gender = :gender) AND " +
           "(:city IS NULL OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(u.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:status IS NULL OR (:status = 'true' AND u.active = true) OR (:status = 'false' AND u.active = false))")
    Page<User> findUsersWithFiltersIncludingDeleted(@Param("name") String name,
                                                   @Param("email") String email,
                                                   @Param("username") String username,
                                                   @Param("contact") String contact,
                                                   @Param("role") User.Role role,
                                                   @Param("gender") User.Gender gender,
                                                   @Param("city") String city,
                                                   @Param("state") String state,
                                                   @Param("status") String status,
                                                   Pageable pageable);
}
