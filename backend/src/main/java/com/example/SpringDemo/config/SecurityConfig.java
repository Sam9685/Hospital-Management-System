package com.example.SpringDemo.config;

import com.example.SpringDemo.service.DoctorUserDetailsService;
import com.example.SpringDemo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    @Qualifier("doctorUserDetailsService")
    private DoctorUserDetailsService doctorUserDetailsService;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private CustomJwtRequestFilter customJwtRequestFilter;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public DaoAuthenticationProvider doctorAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(doctorUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/api/appointments/doctor/my-appointments").hasRole("DOCTOR")
                .requestMatchers("/api/doctors/my-appointments").hasRole("DOCTOR")
                .requestMatchers("/api/appointments/**").permitAll()
                .requestMatchers("/api/doctor-slots/generate-all").hasRole("ADMIN")
                .requestMatchers("/api/doctor-slots/generate-next-week").hasRole("ADMIN")
                .requestMatchers("/api/doctor-slots/generate-initial-slots").permitAll()
                .requestMatchers("/api/doctor-slots/generate-slots").permitAll()
                .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                .requestMatchers("/api/doctors/appointments/**").hasRole("DOCTOR")
                .requestMatchers("/api/doctors").hasRole("ADMIN")
                .requestMatchers("/api/doctors/**").permitAll()
                .requestMatchers("/api/payments/**").permitAll()
                .requestMatchers("/api/profile/test/**").permitAll()
                .requestMatchers("/api/simple-profile/**").permitAll()
                .requestMatchers("/api/simple-complaints/**").permitAll()
                .requestMatchers("/api/data-generation/**").permitAll()
                .requestMatchers("/api/enhanced-data-generation/**").permitAll()
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .authenticationProvider(doctorAuthenticationProvider())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions().disable());
        
        http.addFilterBefore(customJwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
}
