package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simple-profile")
@CrossOrigin(origins = "*")
public class SimpleProfileController {
    
    @Autowired
    private UserService userService;
    
    // Get user by ID - NO AUTHENTICATION REQUIRED
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        try {
            System.out.println("SIMPLE: Getting user by ID: " + userId);
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            System.out.println("SIMPLE: Found user: " + user.getName() + " (ID: " + user.getId() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("SIMPLE: Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get user by email - NO AUTHENTICATION REQUIRED
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        try {
            System.out.println("SIMPLE: Getting user by email: " + email);
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            System.out.println("SIMPLE: Found user: " + user.getName() + " (ID: " + user.getId() + ", Email: " + user.getEmail() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("SIMPLE: Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Get user by username - NO AUTHENTICATION REQUIRED
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        try {
            System.out.println("SIMPLE: Getting user by username: " + username);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            
            System.out.println("SIMPLE: Found user: " + user.getName() + " (ID: " + user.getId() + ", Username: " + user.getUsername() + ")");
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.out.println("SIMPLE: Error getting user by username: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Update user by ID - NO AUTHENTICATION REQUIRED (for testing)
    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUserById(@PathVariable Long userId, @RequestBody Map<String, Object> profileData) {
        try {
            System.out.println("=== SIMPLE UPDATE USER ===");
            System.out.println("User ID: " + userId);
            System.out.println("Update data: " + profileData);
            System.out.println("Data keys: " + profileData.keySet());
            
            // Validate user exists first
            User existingUser = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            System.out.println("Existing user: " + existingUser.getName() + " (ID: " + existingUser.getId() + ")");
            
            // Clean the data - remove null values and empty strings
            Map<String, Object> cleanedData = new HashMap<>();
            for (Map.Entry<String, Object> entry : profileData.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().toString().trim().isEmpty()) {
                    cleanedData.put(entry.getKey(), entry.getValue());
                }
            }
            
            System.out.println("Cleaned data: " + cleanedData);
            
            // Update the user
            User updatedUser = userService.updateUser(userId, cleanedData);
            System.out.println("✅ Updated user: " + updatedUser.getName() + " (ID: " + updatedUser.getId() + ")");
            
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
        } catch (Exception e) {
            System.out.println("❌ Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }
    
    // Test update endpoint - NO AUTHENTICATION REQUIRED (for testing)
    @PutMapping("/test-update/{userId}")
    public ResponseEntity<ApiResponse<User>> testUpdateUser(@PathVariable Long userId) {
        try {
            System.out.println("=== TEST UPDATE ===");
            System.out.println("Testing update for user ID: " + userId);
            
            // Create test data
            Map<String, Object> testData = new HashMap<>();
            testData.put("name", "Test Updated Name");
            testData.put("contact", "1234567890");
            testData.put("city", "Test City");
            
            System.out.println("Test data: " + testData);
            
            User updatedUser = userService.updateUser(userId, testData);
            System.out.println("✅ Test update successful: " + updatedUser.getName());
            
            return ResponseEntity.ok(ApiResponse.success("Test update successful", updatedUser));
        } catch (Exception e) {
            System.out.println("❌ Test update failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Test update failed: " + e.getMessage()));
        }
    }
    
    // Get all users - NO AUTHENTICATION REQUIRED (for testing)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers() {
        try {
            System.out.println("SIMPLE: Getting all users");
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "This endpoint works without authentication");
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.out.println("SIMPLE: Error getting all users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
