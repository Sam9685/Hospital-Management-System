package com.example.SpringDemo.config;

import com.example.SpringDemo.service.UserDetailsServiceImpl;
import com.example.SpringDemo.entity.Session;
import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.entity.Doctor;
import com.example.SpringDemo.repository.SessionRepository;
import com.example.SpringDemo.repository.DoctorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        System.out.println("=== JWT FILTER DEBUG ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Authorization Header: " + requestTokenHeader);
        
        String username = null;
        String jwtToken = null;
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtConfig.extractUsername(jwtToken);
                System.out.println("Extracted username: " + username);
            } catch (Exception e) {
                System.err.println("Unable to get JWT Token or JWT Token has expired: " + e.getMessage());
            }
        } else {
            System.out.println("No Authorization header or invalid format");
        }
        
        if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validate session from database
                Optional<Session> sessionOpt = sessionRepository.findBySessionTokenAndIsActiveTrueAndDeletedAtIsNull(jwtToken);
                
                if (sessionOpt.isPresent()) {
                    Session session = sessionOpt.get();
                    System.out.println("Session found in database: " + session.getId());
                    
                    // Check if session is expired
                    if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
                        System.out.println("Session expired, invalidating...");
                        session.setIsActive(false);
                        session.setUpdatedAt(LocalDateTime.now());
                        sessionRepository.save(session);
                        System.out.println("Session invalidated due to expiry");
                    } else {
                        // Session is valid, check user/doctor status
                        if ("USER".equals(session.getUserType())) {
                            // Handle user session
                            if (session.getUser() != null) {
                                User user = session.getUser();
                                System.out.println("User session for: " + user.getEmail() + ", Active: " + user.getActive());
                                
                                // Check if user is still active
                                if (!user.getActive()) {
                                    System.out.println("User account is inactive, invalidating session");
                                    session.setIsActive(false);
                                    session.setUpdatedAt(LocalDateTime.now());
                                    sessionRepository.save(session);
                                } else {
                                    // User is active, create authentication
                                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());
                                    if (jwtConfig.validateToken(jwtToken, userDetails)) {
                                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                                            new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());
                                        usernamePasswordAuthenticationToken
                                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                        System.out.println("User authentication successful");
                                    }
                                }
                            }
                        } else if ("DOCTOR".equals(session.getUserType())) {
                            // Handle doctor session
                            if (session.getDoctorId() != null) {
                                Optional<Doctor> doctorOpt = doctorRepository.findById(session.getDoctorId());
                                if (doctorOpt.isPresent()) {
                                    Doctor doctor = doctorOpt.get();
                                    System.out.println("Doctor session for: " + doctor.getEmail() + ", Active: " + doctor.getActive());
                                    
                                    // Check if doctor is still active
                                    if (!doctor.getActive()) {
                                        System.out.println("Doctor account is inactive, invalidating session");
                                        session.setIsActive(false);
                                        session.setUpdatedAt(LocalDateTime.now());
                                        sessionRepository.save(session);
                                    } else {
                                        // Doctor is active, create authentication
                                        // For doctors, we need to create a custom UserDetails
                                        UserDetails doctorDetails = createDoctorUserDetails(doctor);
                                        if (jwtConfig.validateToken(jwtToken, doctorDetails)) {
                                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                                                new UsernamePasswordAuthenticationToken(
                                                    doctorDetails, null, doctorDetails.getAuthorities());
                                            usernamePasswordAuthenticationToken
                                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                            System.out.println("Doctor authentication successful");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("No valid session found for token");
                }
            } catch (Exception e) {
                System.err.println("Error validating session: " + e.getMessage());
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }
    
    private UserDetails createDoctorUserDetails(Doctor doctor) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(doctor.getEmail())
                .password(doctor.getPasswordHash())
                .authorities("ROLE_DOCTOR")
                .build();
    }
}
