package com.example.SpringDemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class PasswordTestController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/password-hash")
    public String generatePasswordHash() {
        String password = "Password@123";
        String hash = passwordEncoder.encode(password);
        
        return "Password: " + password + "\nHash: " + hash + "\nVerification: " + passwordEncoder.matches(password, hash);
    }
    
    @GetMapping("/test-current-hash")
    public String testCurrentHash() {
        String password = "Password@123";
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi";
        
        return "Password: " + password + "\nCurrent Hash: " + currentHash + "\nMatches: " + passwordEncoder.matches(password, currentHash);
    }
    
    @GetMapping("/generate-correct-hash")
    public String generateCorrectHash() {
        String password = "Password@123";
        String hash = passwordEncoder.encode(password);
        
        // Return the hash in a format that can be easily copied
        return "UPDATE users SET password_hash = '" + hash + "' WHERE email = 'admin@hospital.com';";
    }
}
