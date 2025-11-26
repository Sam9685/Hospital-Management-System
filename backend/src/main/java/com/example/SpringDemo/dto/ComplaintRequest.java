package com.example.SpringDemo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ComplaintRequest {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    private Long appointmentId;
    
    @NotNull(message = "Category is required")
    private String category;
    
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 500, message = "Description must be between 20 and 500 characters")
    private String description;
    
    @NotNull(message = "Contact preference is required")
    private String contactPreference;
    
    private String priority = "MEDIUM";
}
