package com.example.TripSplit.dto;

import java.math.BigDecimal;

public class ExpenseSplitDTO {
    private Long participantId;
    private String participantName;
    private BigDecimal owedAmount;

    public ExpenseSplitDTO() {}

    public ExpenseSplitDTO(Long participantId, String participantName, BigDecimal owedAmount) {
        this.participantId = participantId;
        this.participantName = participantName;
        this.owedAmount = owedAmount;
    }

    public static ExpenseSplitDTOBuilder builder() {
        return new ExpenseSplitDTOBuilder();
    }

    public static class ExpenseSplitDTOBuilder {
        private Long participantId;
        private String participantName;
        private BigDecimal owedAmount;

        public ExpenseSplitDTOBuilder participantId(Long participantId) { this.participantId = participantId; return this; }
        public ExpenseSplitDTOBuilder participantName(String participantName) { this.participantName = participantName; return this; }
        public ExpenseSplitDTOBuilder owedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; return this; }

        public ExpenseSplitDTO build() {
            return new ExpenseSplitDTO(participantId, participantName, owedAmount);
        }
    }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public BigDecimal getOwedAmount() { return owedAmount; }
    public void setOwedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; }
}
