package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.Session;
import com.example.SpringDemo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SessionController {
    
    @Autowired
    private SessionService sessionService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Session>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expiresAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isActive) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Session> sessions = sessionService.getAllSessions(userId, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Session>>> getSessionsByUser(@PathVariable Long userId) {
        List<Session> sessions = sessionService.getSessionsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Session>>> getActiveSessions(
            @RequestParam(required = false) Long userId) {
        List<Session> sessions = sessionService.getActiveSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Session>> getSessionById(@PathVariable Long id) {
        Optional<Session> session = sessionService.getSessionById(id);
        if (session.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(session.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Session not found"));
        }
    }
    
    @PostMapping("/invalidate-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> invalidateUserSessions(@PathVariable Long userId) {
        try {
            sessionService.invalidateUserSessions(userId);
            return ResponseEntity.ok(ApiResponse.success("All sessions for user invalidated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/invalidate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Session>> invalidateSession(@PathVariable Long id) {
        try {
            Session session = sessionService.invalidateSession(id);
            return ResponseEntity.ok(ApiResponse.success("Session invalidated successfully", session));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getSessionStats() {
        try {
            Object stats = sessionService.getSessionStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
