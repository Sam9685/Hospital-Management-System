package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.dto.LoginRequest;
import com.example.SpringDemo.dto.LoginResponse;
import com.example.SpringDemo.dto.PatientRegistrationRequest;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("=== AUTH CONTROLLER DEBUG ===");
        System.out.println("Received login request for email: " + loginRequest.getEmail());
        System.out.println("Password length: " + (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : "null"));
        
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            System.out.println("Login successful, returning response");
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            System.out.println("Login failed with exception: " + e.getClass().getSimpleName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        try {
            User user = authService.registerPatient(request);
            return ResponseEntity.ok(ApiResponse.success("Patient registered successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                authService.logout(jwtToken);
                return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid token format"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
