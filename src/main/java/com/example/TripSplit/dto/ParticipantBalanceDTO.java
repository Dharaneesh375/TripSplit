package com.example.TripSplit.dto;

import java.math.BigDecimal;

public class ParticipantBalanceDTO {
    private Long participantId;
    private String participantName;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    private BigDecimal netBalance;

    public ParticipantBalanceDTO() {}

    public ParticipantBalanceDTO(Long participantId, String participantName, BigDecimal totalPaid, BigDecimal totalOwed, BigDecimal netBalance) {
        this.participantId = participantId;
        this.participantName = participantName;
        this.totalPaid = totalPaid;
        this.totalOwed = totalOwed;
        this.netBalance = netBalance;
    }

    public static ParticipantBalanceDTOBuilder builder() {
        return new ParticipantBalanceDTOBuilder();
    }

    public static class ParticipantBalanceDTOBuilder {
        private Long participantId;
        private String participantName;
        private BigDecimal totalPaid;
        private BigDecimal totalOwed;
        private BigDecimal netBalance;

        public ParticipantBalanceDTOBuilder participantId(Long participantId) { this.participantId = participantId; return this; }
        public ParticipantBalanceDTOBuilder participantName(String participantName) { this.participantName = participantName; return this; }
        public ParticipantBalanceDTOBuilder totalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; return this; }
        public ParticipantBalanceDTOBuilder totalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; return this; }
        public ParticipantBalanceDTOBuilder netBalance(BigDecimal netBalance) { this.netBalance = netBalance; return this; }

        public ParticipantBalanceDTO build() {
            return new ParticipantBalanceDTO(participantId, participantName, totalPaid, totalOwed, netBalance);
        }
    }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public BigDecimal getTotalOwed() { return totalOwed; }
    public void setTotalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; }

    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }
}
