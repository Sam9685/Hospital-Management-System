package com.example.SpringDemo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash from data.sql for admin user
        String storedHash = "$2a$10$UxUj4o9TMpxLaDfvsMEJK.nOc9lCrrCW4E6u1FLfc5sCyQM6dUwwS";
        
        // Test Password123 (from test-credentials.txt)
        String password123 = "Password123";
        System.out.println("Testing Password123:");
        System.out.println("Password: " + password123);
        System.out.println("Stored Hash: " + storedHash);
        System.out.println("Password123 matches: " + encoder.matches(password123, storedHash));
        
        // Test Password@123 (from other test files)
        String passwordAt123 = "Password@123";
        System.out.println("\nTesting Password@123:");
        System.out.println("Password: " + passwordAt123);
        System.out.println("Password@123 matches: " + encoder.matches(passwordAt123, storedHash));
        
        // Test simple password
        String simplePassword = "password";
        System.out.println("\nTesting password:");
        System.out.println("Password: " + simplePassword);
        System.out.println("password matches: " + encoder.matches(simplePassword, storedHash));
        
        // Generate new hashes for both passwords
        System.out.println("\nGenerating new hashes:");
        String newHash123 = encoder.encode(password123);
        System.out.println("New hash for Password123: " + newHash123);
        
        String newHashAt123 = encoder.encode(passwordAt123);
        System.out.println("New hash for Password@123: " + newHashAt123);
    }
}
