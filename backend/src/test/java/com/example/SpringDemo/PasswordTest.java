package com.example.SpringDemo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test the current hash in data.sql
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi";
        String testPassword = "Password@123";
        
        System.out.println("Testing password: " + testPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Password matches: " + encoder.matches(testPassword, storedHash));
        
        // Generate a new hash for Password@123
        String newHash = encoder.encode(testPassword);
        System.out.println("New hash for Password@123: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(testPassword, newHash));
    }
}