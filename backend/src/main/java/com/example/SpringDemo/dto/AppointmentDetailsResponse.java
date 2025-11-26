package com.example.SpringDemo.dto;

import com.example.SpringDemo.entity.Appointment;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDetailsResponse {
    private Long id;
    private PatientInfo patient;
    private DoctorInfo doctor;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalTime endTime;
    private Appointment.Status status;
    private Appointment.AppointmentType appointmentType;
    private java.math.BigDecimal consultationFee;
    private String symptoms;
    private String notes;
    private String cancelledByName;
    private String cancelledByType; // "DOCTOR" or "USER"
    private Long cancelledByUser;
    private Long cancelledByDoctor;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime createdAt;

    @Data
    public static class PatientInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String contact;
        private String gender;
        private String bloodGroup;
    }

    @Data
    public static class DoctorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String specialization;
        private java.math.BigDecimal consultationFee;
    }
}
