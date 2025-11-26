package com.example.SpringDemo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PatientRegistrationRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters long")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
    private String name;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 25, message = "First name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    private String firstname;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 25, message = "Last name must be between 2 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    private String lastname;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be a valid 10-digit Indian mobile number")
    private String contact;
    
    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+\\d{1,3}$", message = "Country code must be valid (e.g., +91, +1)")
    private String countryCode;
    
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username must contain only letters, numbers, dots, underscores, and hyphens")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "State must contain only letters and spaces")
    private String state;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "City must contain only letters and spaces")
    private String city;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Country must contain only letters and spaces")
    private String country;
    
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be exactly 6 digits")
    private String postalCode;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$|^$", message = "Blood group must be valid (e.g., A+, B-, AB+, O-) or empty")
    private String bloodGroup;
    
    @Size(max = 50, message = "Emergency contact name must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Emergency contact name must contain only letters and spaces")
    private String emergencyContactName;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact number must be a valid 10-digit Indian mobile number")
    private String emergencyContactNum;
    
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;
    
    private String birthdate;
}
