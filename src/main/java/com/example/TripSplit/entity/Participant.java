package com.example.TripSplit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnore
    private Trip trip;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Participant() {}

    public Participant(Long id, String name, String email, Trip trip, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.trip = trip;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static ParticipantBuilder builder() {
        return new ParticipantBuilder();
    }

    public static class ParticipantBuilder {
        private Long id;
        private String name;
        private String email;
        private Trip trip;
        private LocalDateTime createdAt;

        public ParticipantBuilder id(Long id) { this.id = id; return this; }
        public ParticipantBuilder name(String name) { this.name = name; return this; }
        public ParticipantBuilder email(String email) { this.email = email; return this; }
        public ParticipantBuilder trip(Trip trip) { this.trip = trip; return this; }
        public ParticipantBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Participant build() {
            return new Participant(id, name, email, trip, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
