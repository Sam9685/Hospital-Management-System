package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.AuditLog;
import com.example.SpringDemo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public Page<AuditLog> getAllAuditLogs(Long userId, String tableName, String action, 
                                         LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        return auditLogRepository.findAuditLogsWithFilters(userId, tableName, action, 
                                                          fromDate, toDate, pageable);
    }
    
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }
    
    public Page<AuditLog> getAuditLogsByTable(String tableName, Pageable pageable) {
        return auditLogRepository.findByTableName(tableName, pageable);
    }
    
    public Page<AuditLog> searchAuditLogs(Long userId, String tableName, String action, 
                                         Long recordId, String ipAddress, LocalDateTime fromDate, 
                                         LocalDateTime toDate, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, tableName, action, recordId, 
                                                 ipAddress, fromDate, toDate, pageable);
    }
    
    public Optional<AuditLog> getAuditLogById(Long id) {
        return auditLogRepository.findByIdCustom(id);
    }
    
    public Object getAuditLogStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalAuditLogs", auditLogRepository.countAll());
        stats.put("inserts", auditLogRepository.countByAction("INSERT"));
        stats.put("updates", auditLogRepository.countByAction("UPDATE"));
        stats.put("deletes", auditLogRepository.countByAction("DELETE"));
        stats.put("selects", auditLogRepository.countByAction("SELECT"));
        
        return stats;
    }
    
    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditLogRepository.findTop10OrderByCreatedAtDesc();
    }
    
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    public Page<AuditLog> getAllAuditLogs(String action, String tableName, Long userId, String fromDate, String toDate, String status, Pageable pageable) {
        // Convert string action to enum
        AuditLog.Action actionEnum = null;
        if (action != null && !action.isEmpty()) {
            try {
                actionEnum = AuditLog.Action.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid action, will be ignored
            }
        }
        
        // Convert string dates to LocalDateTime
        LocalDateTime fromDateParsed = null;
        if (fromDate != null && !fromDate.isEmpty()) {
            try {
                fromDateParsed = LocalDate.parse(fromDate).atStartOfDay();
            } catch (Exception e) {
                // Invalid date format, will be ignored
            }
        }
        
        LocalDateTime toDateParsed = null;
        if (toDate != null && !toDate.isEmpty()) {
            try {
                toDateParsed = LocalDate.parse(toDate).atTime(23, 59, 59);
            } catch (Exception e) {
                // Invalid date format, will be ignored
            }
        }
        
        return auditLogRepository.findAuditLogsWithFilters(action, actionEnum, tableName, userId, fromDate, fromDateParsed, toDate, toDateParsed, pageable);
    }
    
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop10OrderByCreatedAtDesc();
    }
}
