package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.User;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== USER DETAILS SERVICE DEBUG ===");
        System.out.println("Loading user by email: " + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        
        System.out.println("User found in UserDetailsService:");
        System.out.println("User ID: " + user.getId());
        System.out.println("User Email: " + user.getEmail());
        System.out.println("User Password Hash: " + user.getPasswordHash());
        System.out.println("User Role: " + user.getRole());
        System.out.println("User Active: " + user.getActive());
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        System.out.println("UserPrincipal created with password: " + userPrincipal.getPassword());
        
        return userPrincipal;
    }
}
