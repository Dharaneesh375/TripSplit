package com.example.TripSplit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_id", nullable = false)
    private Participant payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnore
    private Trip trip;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplit> splits = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Expense() {}

    public Expense(Long id, String description, BigDecimal amount, String category, Participant payer, Trip trip, List<ExpenseSplit> splits, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.payer = payer;
        this.trip = trip;
        if (splits != null) this.splits = splits;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.category == null || this.category.isBlank()) {
            this.category = "GENERAL";
        }
    }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String category;
        private Participant payer;
        private Trip trip;
        private List<ExpenseSplit> splits = new ArrayList<>();
        private LocalDateTime createdAt;

        public ExpenseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseBuilder description(String description) { this.description = description; return this; }
        public ExpenseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseBuilder category(String category) { this.category = category; return this; }
        public ExpenseBuilder payer(Participant payer) { this.payer = payer; return this; }
        public ExpenseBuilder trip(Trip trip) { this.trip = trip; return this; }
        public ExpenseBuilder splits(List<ExpenseSplit> splits) { this.splits = splits; return this; }
        public ExpenseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Expense build() {
            return new Expense(id, description, amount, category, payer, trip, splits, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Participant getPayer() { return payer; }
    public void setPayer(Participant payer) { this.payer = payer; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public List<ExpenseSplit> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplit> splits) { this.splits = splits; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
