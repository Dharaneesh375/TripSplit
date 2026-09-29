package com.example.TripSplit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SettlementDTO {
    private Long id;
    private Long fromParticipantId;
    private String fromParticipantName;
    private Long toParticipantId;
    private String toParticipantName;
    private BigDecimal amount;
    private Boolean isSettled;
    private LocalDateTime createdAt;

    public SettlementDTO() {}

    public SettlementDTO(Long id, Long fromParticipantId, String fromParticipantName, Long toParticipantId, String toParticipantName, BigDecimal amount, Boolean isSettled, LocalDateTime createdAt) {
        this.id = id;
        this.fromParticipantId = fromParticipantId;
        this.fromParticipantName = fromParticipantName;
        this.toParticipantId = toParticipantId;
        this.toParticipantName = toParticipantName;
        this.amount = amount;
        this.isSettled = isSettled;
        this.createdAt = createdAt;
    }

    public static SettlementDTOBuilder builder() {
        return new SettlementDTOBuilder();
    }

    public static class SettlementDTOBuilder {
        private Long id;
        private Long fromParticipantId;
        private String fromParticipantName;
        private Long toParticipantId;
        private String toParticipantName;
        private BigDecimal amount;
        private Boolean isSettled;
        private LocalDateTime createdAt;

        public SettlementDTOBuilder id(Long id) { this.id = id; return this; }
        public SettlementDTOBuilder fromParticipantId(Long fromParticipantId) { this.fromParticipantId = fromParticipantId; return this; }
        public SettlementDTOBuilder fromParticipantName(String fromParticipantName) { this.fromParticipantName = fromParticipantName; return this; }
        public SettlementDTOBuilder toParticipantId(Long toParticipantId) { this.toParticipantId = toParticipantId; return this; }
        public SettlementDTOBuilder toParticipantName(String toParticipantName) { this.toParticipantName = toParticipantName; return this; }
        public SettlementDTOBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public SettlementDTOBuilder isSettled(Boolean isSettled) { this.isSettled = isSettled; return this; }
        public SettlementDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SettlementDTO build() {
            return new SettlementDTO(id, fromParticipantId, fromParticipantName, toParticipantId, toParticipantName, amount, isSettled, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFromParticipantId() { return fromParticipantId; }
    public void setFromParticipantId(Long fromParticipantId) { this.fromParticipantId = fromParticipantId; }

    public String getFromParticipantName() { return fromParticipantName; }
    public void setFromParticipantName(String fromParticipantName) { this.fromParticipantName = fromParticipantName; }

    public Long getToParticipantId() { return toParticipantId; }
    public void setToParticipantId(Long toParticipantId) { this.toParticipantId = toParticipantId; }

    public String getToParticipantName() { return toParticipantName; }
    public void setToParticipantName(String toParticipantName) { this.toParticipantName = toParticipantName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Boolean getIsSettled() { return isSettled; }
    public void setIsSettled(Boolean isSettled) { this.isSettled = isSettled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
