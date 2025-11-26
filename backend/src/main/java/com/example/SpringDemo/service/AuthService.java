package com.example.SpringDemo.service;

import com.example.SpringDemo.config.JwtConfig;
import com.example.SpringDemo.dto.LoginRequest;
import com.example.SpringDemo.dto.LoginResponse;
import com.example.SpringDemo.dto.PatientRegistrationRequest;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.entity.Session;
import com.example.SpringDemo.repository.UserRepository;
import com.example.SpringDemo.repository.DoctorRepository;
import com.example.SpringDemo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        System.out.println("=== AUTH SERVICE DEBUG ===");
        System.out.println("Login Request Email: " + loginRequest.getEmail());
        System.out.println("Login Request Password: " + loginRequest.getPassword());
        
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        
        // Check if it's a doctor email (ends with @doctor.com)
        if (email.endsWith("@doctor.com")) {
            System.out.println("Doctor login detected");
            return authenticateDoctor(email, password);
        } else {
            System.out.println("User login detected");
            return authenticateUser(email, password);
        }
    }
    
        private LoginResponse authenticateDoctor(String email, String password) {
            // Find doctor by email
            Doctor doctor = doctorRepository.findByEmail(email).orElse(null);

            if (doctor == null) {
                System.out.println("Doctor not found in database for email: " + email);
                throw new RuntimeException("Invalid credentials");
            }

            System.out.println("Found doctor in database:");
            System.out.println("Doctor ID: " + doctor.getDoctorId());
            System.out.println("Doctor Email: " + doctor.getEmail());
            System.out.println("Doctor Name: " + doctor.getFullName());
            System.out.println("Doctor Active: " + doctor.getActive());

            // Check if doctor is active
            if (!doctor.getActive()) {
                System.out.println("Doctor account is inactive");
                throw new RuntimeException("Account is inactive");
            }

            // Verify password using BCrypt
            if (!passwordEncoder.matches(password, doctor.getPasswordHash())) {
                System.out.println("Invalid doctor password");
                throw new RuntimeException("Invalid credentials");
            }

            System.out.println("Doctor password verified successfully");

            // For doctors, we'll generate JWT directly without using authenticationManager
            // since doctors are not loaded by UserDetailsService
            String jwt = jwtConfig.generateTokenForDoctor(doctor);

            // Create session for doctor
            createSessionForDoctor(doctor, jwt);

            return new LoginResponse(jwt, "Bearer", doctor.getDoctorId(),
                                   doctor.getEmail().split("@")[0], // Use email prefix as username
                                   doctor.getEmail(), "DOCTOR", doctor.getFullName());
        }
    
    private LoginResponse authenticateUser(String email, String password) {
        System.out.println("=== AUTHENTICATING REGULAR USER ===");
        
        // Get user from database to check stored password
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("User not found in database for email: " + email);
            throw new RuntimeException("Invalid credentials");
        }
        
        System.out.println("Found user in database:");
        System.out.println("User ID: " + user.getId());
        System.out.println("User Email: " + user.getEmail());
        System.out.println("User Role: " + user.getRole());
        System.out.println("User Active: " + user.getActive());
        
        // Check if user is active
        if (!user.getActive()) {
            System.out.println("User account is inactive");
            throw new RuntimeException("Account is inactive");
        }
        
        // Verify password using BCrypt
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            System.out.println("Invalid user password");
            throw new RuntimeException("Invalid credentials");
        }
        
        System.out.println("User password verified successfully");
        
        // Generate JWT token directly for users (similar to doctors)
        String jwt = jwtConfig.generateTokenForUser(user);
        
        // Create session for user
        createSessionForUser(user, jwt);
        
        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return new LoginResponse(jwt, "Bearer", user.getId(), user.getUsername(), 
                               user.getEmail(), user.getRole().name(), user.getName());
    }
    
    public User registerPatient(PatientRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        if (userRepository.existsByContact(request.getContact())) {
            throw new RuntimeException("Mobile number is already registered!");
        }
        
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setContact(request.getContact());
        user.setCountryCode(request.getCountryCode());
        user.setAddress(request.getAddress());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.PATIENT);
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setPostalCode(request.getPostalCode());
        user.setBloodGroup(request.getBloodGroup());
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setEmergencyContactNum(request.getEmergencyContactNum());
        
        // Handle gender
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid gender: " + request.getGender());
            }
        }
        
        // Handle birthdate
        if (request.getBirthdate() != null && !request.getBirthdate().isEmpty()) {
            try {
                user.setBirthdate(java.time.LocalDate.parse(request.getBirthdate()));
            } catch (Exception e) {
                throw new RuntimeException("Invalid birthdate format: " + request.getBirthdate());
            }
        }
        
        return userRepository.save(user);
    }
    
    private void createSessionForUser(User user, String jwt) {
        // Invalidate any existing active sessions for this user
        invalidateUserSessions(user.getId());
        
        // Create new session
        Session session = new Session();
        session.setUser(user);
        session.setUserType("USER");
        session.setSessionToken(jwt);
        session.setIsActive(true);
        session.setExpiresAt(LocalDateTime.now().plusDays(7)); // 1 week from now
        session.setCreatedBy(user.getId());
        
        sessionRepository.save(session);
        System.out.println("Session created for user: " + user.getEmail());
    }
    
    private void createSessionForDoctor(Doctor doctor, String jwt) {
        // Invalidate any existing active sessions for this doctor
        invalidateDoctorSessions(doctor.getDoctorId());
        
        // Create new session for doctor
        Session session = new Session();
        session.setUser(null); // No user for doctors
        session.setDoctorId(doctor.getDoctorId());
        session.setUserType("DOCTOR");
        session.setSessionToken(jwt);
        session.setIsActive(true);
        session.setExpiresAt(LocalDateTime.now().plusDays(7)); // 1 week from now
        session.setCreatedBy(doctor.getDoctorId());
        
        sessionRepository.save(session);
        System.out.println("Session created for doctor: " + doctor.getEmail());
    }
    
    private void invalidateUserSessions(Long userId) {
        // Invalidate all active sessions for the user
        sessionRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(userId)
            .forEach(session -> {
                session.setIsActive(false);
                session.setUpdatedAt(LocalDateTime.now());
                sessionRepository.save(session);
            });
    }
    
    private void invalidateDoctorSessions(Long doctorId) {
        // Invalidate all active sessions for the doctor
        sessionRepository.findByDoctorIdAndIsActiveTrueAndDeletedAtIsNull(doctorId)
            .forEach(session -> {
                session.setIsActive(false);
                session.setUpdatedAt(LocalDateTime.now());
                sessionRepository.save(session);
            });
    }
    
    public void logout(String jwtToken) {
        try {
            // Find and invalidate the session
            sessionRepository.findBySessionTokenAndIsActiveTrueAndDeletedAtIsNull(jwtToken)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    session.setUpdatedAt(LocalDateTime.now());
                    sessionRepository.save(session);
                    System.out.println("Session invalidated for logout: " + session.getId());
                });
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            throw new RuntimeException("Logout failed");
        }
    }
}
