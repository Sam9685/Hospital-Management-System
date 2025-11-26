package com.example.SpringDemo.service;

import com.example.SpringDemo.dto.UserRequest;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(UserRequest request) {
        // Get current user's role for hierarchy validation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserRole = "ADMIN"; // Default to ADMIN for now
        
        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            currentUserRole = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        }
        
        // Validate role hierarchy
        User.Role requestedRole;
        try {
            requestedRole = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.getRole());
        }
        
        // Check role hierarchy
        if (currentUserRole.equals("ADMIN") && !requestedRole.equals(User.Role.PATIENT)) {
            throw new RuntimeException("Admin can only create patient users");
        }
        if (currentUserRole.equals("ADMIN") && 
            !requestedRole.equals(User.Role.PATIENT) && !requestedRole.equals(User.Role.ADMIN)) {
            throw new RuntimeException("SuperAdmin can only create patient or admin users");
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(requestedRole);
        user.setContact(request.getContact());
        user.setBirthdate(request.getBirthdate());
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setEmergencyContactNum(request.getEmergencyContactNum());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());
        user.setCountry(request.getCountry());
        user.setCountryCode(request.getCountryCode());
        user.setPostalCode(request.getPostalCode());
        user.setBloodGroup(request.getBloodGroup());
        user.setProfileUrl(request.getProfileUrl());
        
        // Set gender if provided
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid gender: " + request.getGender());
            }
        }
        
        // Set audit fields
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Page<User> getAllUsers(String name, String email, String role, String gender, String status, Pageable pageable) {
        User.Role roleEnum = null;
        if (role != null && !role.isEmpty()) {
            try {
                roleEnum = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid role, will be ignored
            }
        }
        
        User.Gender genderEnum = null;
        if (gender != null && !gender.isEmpty()) {
            try {
                genderEnum = User.Gender.valueOf(gender.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid gender, will be ignored
            }
        }
        
        // Use the method that includes deleted records for display purposes
        return userRepository.findUsersWithFiltersIncludingDeleted(name, email, null, null, roleEnum, genderEnum, null, null, status, pageable);
    }
    
    public Page<User> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    public Page<User> searchUsers(String name, String email, String username, String contact, 
                                 String role, String gender, String city, String state, Pageable pageable) {
        User.Role roleEnum = null;
        if (role != null && !role.isEmpty()) {
            try {
                roleEnum = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid role, will be ignored
            }
        }
        
        User.Gender genderEnum = null;
        if (gender != null && !gender.isEmpty()) {
            try {
                genderEnum = User.Gender.valueOf(gender.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid gender, will be ignored
            }
        }
        
        return userRepository.searchUsers(name, email, username, contact, roleEnum, 
                                         genderEnum, city, state, pageable);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        // Prevent changing active status of admin users
        if (userDetails.getActive() != null && user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Cannot change active status of admin users");
        }
        
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getFirstname() != null) user.setFirstname(userDetails.getFirstname());
        if (userDetails.getLastname() != null) user.setLastname(userDetails.getLastname());
        
        // Auto-update name field when firstname or lastname is updated
        if (userDetails.getFirstname() != null || userDetails.getLastname() != null) {
            String firstName = user.getFirstname() != null ? user.getFirstname() : "";
            String lastName = user.getLastname() != null ? user.getLastname() : "";
            String fullName = (firstName + " " + lastName).trim();
            user.setName(fullName);
        }
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getContact() != null) user.setContact(userDetails.getContact());
        if (userDetails.getCountryCode() != null) user.setCountryCode(userDetails.getCountryCode());
        if (userDetails.getAddress() != null) user.setAddress(userDetails.getAddress());
        if (userDetails.getCity() != null) user.setCity(userDetails.getCity());
        if (userDetails.getState() != null) user.setState(userDetails.getState());
        if (userDetails.getCountry() != null) user.setCountry(userDetails.getCountry());
        if (userDetails.getPostalCode() != null) user.setPostalCode(userDetails.getPostalCode());
        if (userDetails.getGender() != null) user.setGender(userDetails.getGender());
        if (userDetails.getBirthdate() != null) user.setBirthdate(userDetails.getBirthdate());
        if (userDetails.getBloodGroup() != null) user.setBloodGroup(userDetails.getBloodGroup());
        if (userDetails.getEmergencyContactName() != null) user.setEmergencyContactName(userDetails.getEmergencyContactName());
        if (userDetails.getEmergencyContactNum() != null) user.setEmergencyContactNum(userDetails.getEmergencyContactNum());
        if (userDetails.getProfileUrl() != null) user.setProfileUrl(userDetails.getProfileUrl());
        if (userDetails.getActive() != null) user.setActive(userDetails.getActive());
        
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getId());
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, Map<String, Object> userDetails) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent changing active status of admin users
        if (userDetails.containsKey("active") && user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Cannot change active status of admin users");
        }
        
        // Update fields from Map
        if (userDetails.containsKey("name")) user.setName((String) userDetails.get("name"));
        if (userDetails.containsKey("firstname")) user.setFirstname((String) userDetails.get("firstname"));
        if (userDetails.containsKey("lastname")) user.setLastname((String) userDetails.get("lastname"));
        
        // Auto-update name field when firstname or lastname is updated
        if (userDetails.containsKey("firstname") || userDetails.containsKey("lastname")) {
            String firstName = user.getFirstname() != null ? user.getFirstname() : "";
            String lastName = user.getLastname() != null ? user.getLastname() : "";
            String fullName = (firstName + " " + lastName).trim();
            user.setName(fullName);
        }
        if (userDetails.containsKey("birthdate")) {
            Object birthdateObj = userDetails.get("birthdate");
            if (birthdateObj instanceof LocalDate) {
                user.setBirthdate((LocalDate) birthdateObj);
            } else if (birthdateObj instanceof String) {
                try {
                    LocalDate birthdate = LocalDate.parse((String) birthdateObj);
                    user.setBirthdate(birthdate);
                } catch (Exception e) {
                    System.out.println("Invalid birthdate format: " + birthdateObj);
                }
            }
        }
        if (userDetails.containsKey("contact")) user.setContact((String) userDetails.get("contact"));
        if (userDetails.containsKey("countryCode")) user.setCountryCode((String) userDetails.get("countryCode"));
        if (userDetails.containsKey("gender")) {
            Object genderObj = userDetails.get("gender");
            if (genderObj instanceof String) {
                try {
                    User.Gender gender = User.Gender.valueOf(((String) genderObj).toUpperCase());
                    user.setGender(gender);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid gender value: " + genderObj);
                }
            } else if (genderObj instanceof User.Gender) {
                user.setGender((User.Gender) genderObj);
            }
        }
        if (userDetails.containsKey("address")) user.setAddress((String) userDetails.get("address"));
        if (userDetails.containsKey("city")) user.setCity((String) userDetails.get("city"));
        if (userDetails.containsKey("state")) user.setState((String) userDetails.get("state"));
        if (userDetails.containsKey("country")) user.setCountry((String) userDetails.get("country"));
        if (userDetails.containsKey("postalCode")) user.setPostalCode((String) userDetails.get("postalCode"));
        if (userDetails.containsKey("bloodGroup")) user.setBloodGroup((String) userDetails.get("bloodGroup"));
        if (userDetails.containsKey("emergencyContactName")) user.setEmergencyContactName((String) userDetails.get("emergencyContactName"));
        if (userDetails.containsKey("emergencyContactNum")) user.setEmergencyContactNum((String) userDetails.get("emergencyContactNum"));
        if (userDetails.containsKey("profileUrl")) user.setProfileUrl((String) userDetails.get("profileUrl"));
        if (userDetails.containsKey("active")) {
            Object activeObj = userDetails.get("active");
            if (activeObj instanceof Boolean) {
                user.setActive((Boolean) activeObj);
            } else if (activeObj instanceof String) {
                user.setActive(Boolean.parseBoolean((String) activeObj));
            }
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getId());
        
        return userRepository.save(user);
    }
    
    public User updateUserStatus(Long id, String status) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Assuming we have a status field in User entity
        // If not, we can use a different approach like setting deletedAt for deactivation
        if ("ACTIVE".equals(status)) {
            user.setDeletedAt(null);
        } else if ("INACTIVE".equals(status)) {
            user.setDeletedAt(LocalDateTime.now());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getId());
        
        return userRepository.save(user);
    }
    
    public User updateUserRole(Long id, String role) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            User.Role roleEnum = User.Role.valueOf(role.toUpperCase());
            user.setRole(roleEnum);
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy(user.getId());
            
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(user.getId());
        userRepository.save(user);
        
        // Note: Doctors are now independent entities, so no need to delete doctor records when deleting users
    }
    
    public Object getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.countByDeletedAtIsNull());
        stats.put("totalPatients", userRepository.countByRoleAndDeletedAtIsNull(User.Role.PATIENT));
        stats.put("totalAdmins", userRepository.countByRoleAndDeletedAtIsNull(User.Role.ADMIN));
        stats.put("activeUsers", userRepository.countByDeletedAtIsNull());
        
        return stats;
    }
    
    public List<User> getPatients() {
        return userRepository.findByRole(User.Role.PATIENT);
    }

    public List<User> getAdmins() {
        return userRepository.findByRoleIn(List.of(User.Role.ADMIN));
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsersSince(LocalDateTime.now().minusDays(30));
    }
}
