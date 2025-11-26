package com.example.SpringDemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
    @Column(nullable = false)
    private String name;
    
    @Past(message = "Birth date must be in the past")
    @Column(name = "birthdate")
    private LocalDate birthdate;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 25, message = "First name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    @Column(nullable = false)
    private String firstname;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 25, message = "Last name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    @Column(nullable = false)
    private String lastname;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username must contain only letters, numbers, dots, underscores, and hyphens")
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be a valid 10-digit Indian mobile number")
    private String contact;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Size(max = 50, message = "Emergency contact name must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Emergency contact name must contain only letters and spaces")
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact number must be a valid 10-digit Indian mobile number")
    @Column(name = "emergency_contact_num")
    private String emergencyContactNum;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "State must contain only letters and spaces")
    private String state;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "City must contain only letters and spaces")
    private String city;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Country must contain only letters and spaces")
    private String country;
    
    @Pattern(regexp = "^\\+\\d{1,3}$", message = "Country code must be valid (e.g., +91, +1)")
    @Column(name = "country_code")
    private String countryCode;
    
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be exactly 6 digits")
    @Column(name = "postal_code")
    private String postalCode;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$|^$", message = "Blood group must be valid (e.g., A+, B-, AB+, O-) or empty")
    @Column(name = "blood_group")
    private String bloodGroup;
    
    @Column(name = "profile_url")
    private String profileUrl;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Audit fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    public enum Role {
        ADMIN, PATIENT
    }
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
