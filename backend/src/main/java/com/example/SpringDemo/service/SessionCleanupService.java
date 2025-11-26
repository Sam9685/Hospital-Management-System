package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.Session;
import com.example.SpringDemo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionCleanupService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    /**
     * Clean up expired sessions every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredSessions() {
        System.out.println("Starting session cleanup at: " + LocalDateTime.now());
        
        try {
            // Find all expired active sessions
            List<Session> expiredSessions = sessionRepository.findExpiredActiveSessions(LocalDateTime.now());
            
            if (!expiredSessions.isEmpty()) {
                System.out.println("Found " + expiredSessions.size() + " expired sessions to clean up");
                
                // Mark expired sessions as inactive
                for (Session session : expiredSessions) {
                    session.setIsActive(false);
                    session.setUpdatedAt(LocalDateTime.now());
                    sessionRepository.save(session);
                }
                
                System.out.println("Successfully cleaned up " + expiredSessions.size() + " expired sessions");
            } else {
                System.out.println("No expired sessions found");
            }
        } catch (Exception e) {
            System.err.println("Error during session cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clean up old inactive sessions (older than 30 days) - runs daily
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Transactional
    public void cleanupOldInactiveSessions() {
        System.out.println("Starting old inactive session cleanup at: " + LocalDateTime.now());
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            
            // Find old inactive sessions
            List<Session> oldSessions = sessionRepository.findByIsActiveFalseAndUpdatedAtBeforeAndDeletedAtIsNull(cutoffDate);
            
            if (!oldSessions.isEmpty()) {
                System.out.println("Found " + oldSessions.size() + " old inactive sessions to delete");
                
                // Soft delete old sessions
                for (Session session : oldSessions) {
                    session.setDeletedAt(LocalDateTime.now());
                    session.setDeletedBy(session.getUser() != null ? session.getUser().getId() : session.getDoctorId());
                    sessionRepository.save(session);
                }
                
                System.out.println("Successfully deleted " + oldSessions.size() + " old inactive sessions");
            } else {
                System.out.println("No old inactive sessions found");
            }
        } catch (Exception e) {
            System.err.println("Error during old session cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
