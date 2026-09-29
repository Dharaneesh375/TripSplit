package com.example.TripSplit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(Long id, Long tripId, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.tripId = tripId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private Long id;
        private Long tripId;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        public AuditLogBuilder id(Long id) { this.id = id; return this; }
        public AuditLogBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public AuditLogBuilder action(String action) { this.action = action; return this; }
        public AuditLogBuilder details(String details) { this.details = details; return this; }
        public AuditLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AuditLog build() {
            return new AuditLog(id, tripId, action, details, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
