package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("doctorUserDetailsService")
public class DoctorUserDetailsService implements UserDetailsService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== DOCTOR USER DETAILS SERVICE DEBUG ===");
        System.out.println("Loading doctor by email: " + email);
        
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor Not Found with email: " + email));
        
        System.out.println("Doctor found in DoctorUserDetailsService:");
        System.out.println("Doctor ID: " + doctor.getDoctorId());
        System.out.println("Doctor Email: " + doctor.getEmail());
        System.out.println("Doctor Password Hash: " + doctor.getPasswordHash());
        System.out.println("Doctor Active: " + doctor.getActive());
        
        DoctorPrincipal doctorPrincipal = DoctorPrincipal.create(doctor);
        System.out.println("DoctorPrincipal created with password: " + doctorPrincipal.getPassword());
        
        return doctorPrincipal;
    }
}
