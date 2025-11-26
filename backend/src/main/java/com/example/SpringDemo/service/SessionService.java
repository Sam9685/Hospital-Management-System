package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.Session;
import com.example.SpringDemo.repository.SessionRepository;
import com.example.SpringDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SessionService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<Session> getAllSessions(Long userId, Boolean isActive, Pageable pageable) {
        return sessionRepository.findSessionsWithFilters(userId, isActive, pageable);
    }
    
    public List<Session> getSessionsByUser(Long userId) {
        return sessionRepository.findByUserIdAndDeletedAtIsNull(userId);
    }
    
    public List<Session> getActiveSessions(Long userId) {
        if (userId != null) {
            return sessionRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(userId);
        } else {
            return sessionRepository.findByIsActiveTrueAndDeletedAtIsNull();
        }
    }
    
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findByIdAndDeletedAtIsNull(id);
    }
    
    public void invalidateUserSessions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Session> sessions = sessionRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(userId);
        for (Session session : sessions) {
            session.setIsActive(false);
            session.setUpdatedAt(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }
    
    public Session invalidateSession(Long id) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        session.setIsActive(false);
        session.setUpdatedAt(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }
    
    public void deleteSession(Long id) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        session.setDeletedAt(LocalDateTime.now());
        session.setDeletedBy(session.getUser().getId());
        sessionRepository.save(session);
    }
    
    public Object getSessionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalSessions", sessionRepository.countByDeletedAtIsNull());
        stats.put("activeSessions", sessionRepository.countByIsActiveTrueAndDeletedAtIsNull());
        stats.put("inactiveSessions", sessionRepository.countByIsActiveFalseAndDeletedAtIsNull());
        
        return stats;
    }
}
