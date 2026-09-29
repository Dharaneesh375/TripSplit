package com.example.TripSplit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnore
    private Trip trip;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_participant_id", nullable = false)
    private Participant fromParticipant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_participant_id", nullable = false)
    private Participant toParticipant;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "is_settled", nullable = false)
    private Boolean isSettled = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Settlement() {}

    public Settlement(Long id, Trip trip, Participant fromParticipant, Participant toParticipant, BigDecimal amount, Boolean isSettled, LocalDateTime createdAt) {
        this.id = id;
        this.trip = trip;
        this.fromParticipant = fromParticipant;
        this.toParticipant = toParticipant;
        this.amount = amount;
        this.isSettled = isSettled != null ? isSettled : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static SettlementBuilder builder() {
        return new SettlementBuilder();
    }

    public static class SettlementBuilder {
        private Long id;
        private Trip trip;
        private Participant fromParticipant;
        private Participant toParticipant;
        private BigDecimal amount;
        private Boolean isSettled = false;
        private LocalDateTime createdAt;

        public SettlementBuilder id(Long id) { this.id = id; return this; }
        public SettlementBuilder trip(Trip trip) { this.trip = trip; return this; }
        public SettlementBuilder fromParticipant(Participant fromParticipant) { this.fromParticipant = fromParticipant; return this; }
        public SettlementBuilder toParticipant(Participant toParticipant) { this.toParticipant = toParticipant; return this; }
        public SettlementBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public SettlementBuilder isSettled(Boolean isSettled) { this.isSettled = isSettled; return this; }
        public SettlementBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Settlement build() {
            return new Settlement(id, trip, fromParticipant, toParticipant, amount, isSettled, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Participant getFromParticipant() { return fromParticipant; }
    public void setFromParticipant(Participant fromParticipant) { this.fromParticipant = fromParticipant; }

    public Participant getToParticipant() { return toParticipant; }
    public void setToParticipant(Participant toParticipant) { this.toParticipant = toParticipant; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Boolean getIsSettled() { return isSettled; }
    public void setIsSettled(Boolean isSettled) { this.isSettled = isSettled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
