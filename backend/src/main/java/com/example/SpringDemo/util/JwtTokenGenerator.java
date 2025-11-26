package com.example.SpringDemo.util;

import com.example.SpringDemo.config.JwtConfig;
import com.example.SpringDemo.service.UserPrincipal;
import com.example.SpringDemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    public String generateTokenForSuperAdmin() {
        // Create a mock User object for superadmin
        User superAdmin = new User();
        superAdmin.setId(8L);
        superAdmin.setEmail("superadmin@hospital.com");
        superAdmin.setPasswordHash("$2a$10$y./11hZtBLzprDaWjebW/ezLPhfTzETs.MXpW73e3F05.4Q8ZhrEW");
        superAdmin.setRole(User.Role.ADMIN);
        
        UserPrincipal userPrincipal = UserPrincipal.create(superAdmin);
        return jwtConfig.generateToken(userPrincipal);
    }
    
    public static void main(String[] args) {
        // This will be used to generate a test token
        System.out.println("Use this to generate a JWT token for testing");
    }
}
