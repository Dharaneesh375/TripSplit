package com.example.TripSplit.controller;

import com.example.TripSplit.entity.AuditLog;
import com.example.TripSplit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/audit-logs")
@Tag(name = "Audit Log & Accountability", description = "Endpoints for viewing trip activity audit logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "View trip audit logs", description = "Fetch audit log history recording who changed what and when")
    public ResponseEntity<List<AuditLog>> getAuditLogs(@PathVariable Long tripId) {
        return ResponseEntity.ok(auditLogService.getLogsForTrip(tripId));
    }
}
