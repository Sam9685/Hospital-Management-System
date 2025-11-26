package com.example.SpringDemo.config;

import com.example.SpringDemo.service.DoctorUserDetailsService;
import com.example.SpringDemo.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomJwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    @Qualifier("doctorUserDetailsService")
    private DoctorUserDetailsService doctorUserDetailsService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        System.out.println("=== CUSTOM JWT FILTER DEBUG ===");
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
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            
            // Try to load as regular user first (for admin/patient login)
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("Loaded as user: " + username);
            } catch (Exception e) {
                System.out.println("Not found as user, trying as doctor: " + e.getMessage());
                
                // If not found as user, try as doctor
                try {
                    userDetails = doctorUserDetailsService.loadUserByUsername(username);
                    System.out.println("Loaded as doctor: " + username);
                } catch (Exception ex) {
                    System.err.println("User not found in both user and doctor tables: " + ex.getMessage());
                }
            }
            
            if (userDetails != null && jwtConfig.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                
                System.out.println("Authentication set for: " + username + " with authorities: " + userDetails.getAuthorities());
            }
        }
        chain.doFilter(request, response);
    }
}
