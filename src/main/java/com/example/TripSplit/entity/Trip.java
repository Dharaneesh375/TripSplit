package com.example.TripSplit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("trip")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("trip")
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("trip")
    private List<Settlement> settlements = new ArrayList<>();

    public Trip() {}

    public Trip(Long id, String title, String description, String currency, LocalDateTime createdAt, List<Participant> participants, List<Expense> expenses, List<Settlement> settlements) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currency = currency;
        this.createdAt = createdAt;
        if (participants != null) this.participants = participants;
        if (expenses != null) this.expenses = expenses;
        if (settlements != null) this.settlements = settlements;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currency == null || this.currency.isBlank()) {
            this.currency = "INR";
        }
    }

    public static TripBuilder builder() {
        return new TripBuilder();
    }

    public static class TripBuilder {
        private Long id;
        private String title;
        private String description;
        private String currency = "INR";
        private LocalDateTime createdAt;
        private List<Participant> participants = new ArrayList<>();
        private List<Expense> expenses = new ArrayList<>();
        private List<Settlement> settlements = new ArrayList<>();

        public TripBuilder id(Long id) { this.id = id; return this; }
        public TripBuilder title(String title) { this.title = title; return this; }
        public TripBuilder description(String description) { this.description = description; return this; }
        public TripBuilder currency(String currency) { this.currency = currency; return this; }
        public TripBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TripBuilder participants(List<Participant> participants) { this.participants = participants; return this; }
        public TripBuilder expenses(List<Expense> expenses) { this.expenses = expenses; return this; }
        public TripBuilder settlements(List<Settlement> settlements) { this.settlements = settlements; return this; }

        public Trip build() {
            return new Trip(id, title, description, currency, createdAt, participants, expenses, settlements);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Participant> getParticipants() { return participants; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }

    public List<Settlement> getSettlements() { return settlements; }
    public void setSettlements(List<Settlement> settlements) { this.settlements = settlements; }
}
