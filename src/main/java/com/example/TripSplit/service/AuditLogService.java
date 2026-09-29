package com.example.TripSplit.service;

import com.example.TripSplit.entity.AuditLog;
import com.example.TripSplit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(Long tripId, String action, String details) {
        AuditLog auditLog = AuditLog.builder()
                .tripId(tripId)
                .action(action)
                .details(details)
                .build();
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsForTrip(Long tripId) {
        return auditLogRepository.findByTripIdOrderByTimestampDesc(tripId);
    }
}
