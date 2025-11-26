package com.example.SpringDemo.controller;

import com.example.SpringDemo.dto.ApiResponse;
import com.example.SpringDemo.entity.AuditLog;
import com.example.SpringDemo.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@CrossOrigin(origins = "*")
public class AdminAuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status) {
        
        System.out.println("=== ADMIN AUDIT LOGS API DEBUG ===");
        System.out.println("Page: " + page + ", Size: " + size);
        System.out.println("Sort: " + sortBy + " " + sortDir);
        System.out.println("Filters - Action: " + action + ", TableName: " + tableName + ", UserId: " + userId + ", FromDate: " + fromDate + ", ToDate: " + toDate + ", Status: " + status);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AuditLog> auditLogs = auditLogService.getAllAuditLogs(action, tableName, userId, fromDate, toDate, status, pageable);
        System.out.println("Found " + auditLogs.getTotalElements() + " audit logs");
        
        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }
    
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentAuditLogs() {
        try {
            List<AuditLog> recentLogs = auditLogService.getRecentAuditLogs();
            return ResponseEntity.ok(ApiResponse.success(recentLogs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
