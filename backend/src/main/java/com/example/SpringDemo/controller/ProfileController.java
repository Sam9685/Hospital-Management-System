package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.service.UserService;
import com.example.SpringDemo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    @GetMapping
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile() {
        try {
            System.out.println("=== GETTING CURRENT USER PROFILE ===");
            User user = securityUtils.getCurrentUser();
            System.out.println("Found user: " + user.getName() + " (ID: " + user.getId() + ", Email: " + user.getEmail() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("Error getting profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        try {
            System.out.println("Getting user by ID: " + userId);
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            System.out.println("Found user: " + user.getName() + " (ID: " + user.getId() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<User>> updateCurrentUserProfile(@RequestBody Map<String, Object> profileData) {
        try {
            System.out.println("=== UPDATING CURRENT USER PROFILE ===");
            User currentUser = securityUtils.getCurrentUser();
            System.out.println("Updating user: " + currentUser.getName() + " (ID: " + currentUser.getId() + ")");
            
            // Validate and update only allowed fields
            Map<String, Object> allowedFields = new HashMap<>();
            
            // Basic info
            if (profileData.containsKey("name") && profileData.get("name") != null) {
                allowedFields.put("name", profileData.get("name").toString().trim());
            }
            if (profileData.containsKey("firstname") && profileData.get("firstname") != null) {
                allowedFields.put("firstname", profileData.get("firstname").toString().trim());
            }
            if (profileData.containsKey("lastname") && profileData.get("lastname") != null) {
                allowedFields.put("lastname", profileData.get("lastname").toString().trim());
            }
            if (profileData.containsKey("birthdate") && profileData.get("birthdate") != null) {
                try {
                    LocalDate birthdate = LocalDate.parse(profileData.get("birthdate").toString());
                    allowedFields.put("birthdate", birthdate);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Invalid birthdate format"));
                }
            }
            
            // Contact info
            if (profileData.containsKey("contact") && profileData.get("contact") != null) {
                allowedFields.put("contact", profileData.get("contact").toString().trim());
            }
            if (profileData.containsKey("countryCode") && profileData.get("countryCode") != null) {
                allowedFields.put("countryCode", profileData.get("countryCode").toString().trim());
            }
            
            // Gender
            if (profileData.containsKey("gender") && profileData.get("gender") != null) {
                try {
                    User.Gender gender = User.Gender.valueOf(profileData.get("gender").toString().toUpperCase());
                    allowedFields.put("gender", gender);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Invalid gender value"));
                }
            }
            
            // Address info
            if (profileData.containsKey("address") && profileData.get("address") != null) {
                allowedFields.put("address", profileData.get("address").toString().trim());
            }
            if (profileData.containsKey("city") && profileData.get("city") != null) {
                allowedFields.put("city", profileData.get("city").toString().trim());
            }
            if (profileData.containsKey("state") && profileData.get("state") != null) {
                allowedFields.put("state", profileData.get("state").toString().trim());
            }
            if (profileData.containsKey("country") && profileData.get("country") != null) {
                allowedFields.put("country", profileData.get("country").toString().trim());
            }
            if (profileData.containsKey("postalCode") && profileData.get("postalCode") != null) {
                allowedFields.put("postalCode", profileData.get("postalCode").toString().trim());
            }
            
            // Medical info
            if (profileData.containsKey("bloodGroup") && profileData.get("bloodGroup") != null) {
                allowedFields.put("bloodGroup", profileData.get("bloodGroup").toString().trim());
            }
            
            // Emergency contacts
            if (profileData.containsKey("emergencyContactName") && profileData.get("emergencyContactName") != null) {
                allowedFields.put("emergencyContactName", profileData.get("emergencyContactName").toString().trim());
            }
            if (profileData.containsKey("emergencyContactNum") && profileData.get("emergencyContactNum") != null) {
                allowedFields.put("emergencyContactNum", profileData.get("emergencyContactNum").toString().trim());
            }
            
            // Profile URL
            if (profileData.containsKey("profileUrl") && profileData.get("profileUrl") != null) {
                allowedFields.put("profileUrl", profileData.get("profileUrl").toString().trim());
            }
            
            User updatedUser = userService.updateUser(currentUser.getId(), allowedFields);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/public-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicProfileInfo() {
        try {
            User user = securityUtils.getCurrentUser();
            
            Map<String, Object> publicInfo = new HashMap<>();
            publicInfo.put("id", user.getId());
            publicInfo.put("name", user.getName());
            publicInfo.put("email", user.getEmail());
            publicInfo.put("role", user.getRole());
            publicInfo.put("profileUrl", user.getProfileUrl());
            publicInfo.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success(publicInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/test/{userId}")
    public ResponseEntity<ApiResponse<User>> testGetUserById(@PathVariable Long userId) {
        try {
            System.out.println("TEST: Getting user by ID: " + userId);
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            System.out.println("TEST: Found user: " + user.getName() + " (ID: " + user.getId() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("TEST: Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/test/email/{email}")
    public ResponseEntity<ApiResponse<User>> testGetUserByEmail(@PathVariable String email) {
        try {
            System.out.println("TEST: Getting user by email: " + email);
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            System.out.println("TEST: Found user: " + user.getName() + " (ID: " + user.getId() + ", Email: " + user.getEmail() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("TEST: Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
