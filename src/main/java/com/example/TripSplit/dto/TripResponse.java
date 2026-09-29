package com.example.TripSplit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TripResponse {
    private Long id;
    private String title;
    private String description;
    private String currency;
    private LocalDateTime createdAt;
    private List<ParticipantDTO> participants;
    private BigDecimal totalExpenses;
    private int expenseCount;

    public TripResponse() {}

    public TripResponse(Long id, String title, String description, String currency, LocalDateTime createdAt, List<ParticipantDTO> participants, BigDecimal totalExpenses, int expenseCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currency = currency;
        this.createdAt = createdAt;
        this.participants = participants;
        this.totalExpenses = totalExpenses;
        this.expenseCount = expenseCount;
    }

    public static TripResponseBuilder builder() {
        return new TripResponseBuilder();
    }

    public static class TripResponseBuilder {
        private Long id;
        private String title;
        private String description;
        private String currency;
        private LocalDateTime createdAt;
        private List<ParticipantDTO> participants;
        private BigDecimal totalExpenses;
        private int expenseCount;

        public TripResponseBuilder id(Long id) { this.id = id; return this; }
        public TripResponseBuilder title(String title) { this.title = title; return this; }
        public TripResponseBuilder description(String description) { this.description = description; return this; }
        public TripResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public TripResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TripResponseBuilder participants(List<ParticipantDTO> participants) { this.participants = participants; return this; }
        public TripResponseBuilder totalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; return this; }
        public TripResponseBuilder expenseCount(int expenseCount) { this.expenseCount = expenseCount; return this; }

        public TripResponse build() {
            return new TripResponse(id, title, description, currency, createdAt, participants, totalExpenses, expenseCount);
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

    public List<ParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantDTO> participants) { this.participants = participants; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public int getExpenseCount() { return expenseCount; }
    public void setExpenseCount(int expenseCount) { this.expenseCount = expenseCount; }
}
