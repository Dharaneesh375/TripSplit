package com.example.TripSplit.dto;

import java.math.BigDecimal;
import java.util.List;

public class TripBalanceSummaryDTO {
    private Long tripId;
    private String tripTitle;
    private String currency;
    private BigDecimal totalSpending;
    private List<ParticipantBalanceDTO> balances;
    private BigDecimal netBalanceSum;
    private Boolean isBalanceValid;

    public TripBalanceSummaryDTO() {}

    public TripBalanceSummaryDTO(Long tripId, String tripTitle, String currency, BigDecimal totalSpending, List<ParticipantBalanceDTO> balances, BigDecimal netBalanceSum, Boolean isBalanceValid) {
        this.tripId = tripId;
        this.tripTitle = tripTitle;
        this.currency = currency;
        this.totalSpending = totalSpending;
        this.balances = balances;
        this.netBalanceSum = netBalanceSum;
        this.isBalanceValid = isBalanceValid;
    }

    public static TripBalanceSummaryDTOBuilder builder() {
        return new TripBalanceSummaryDTOBuilder();
    }

    public static class TripBalanceSummaryDTOBuilder {
        private Long tripId;
        private String tripTitle;
        private String currency;
        private BigDecimal totalSpending;
        private List<ParticipantBalanceDTO> balances;
        private BigDecimal netBalanceSum;
        private Boolean isBalanceValid;

        public TripBalanceSummaryDTOBuilder tripId(Long tripId) { this.tripId = tripId; return this; }
        public TripBalanceSummaryDTOBuilder tripTitle(String tripTitle) { this.tripTitle = tripTitle; return this; }
        public TripBalanceSummaryDTOBuilder currency(String currency) { this.currency = currency; return this; }
        public TripBalanceSummaryDTOBuilder totalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; return this; }
        public TripBalanceSummaryDTOBuilder balances(List<ParticipantBalanceDTO> balances) { this.balances = balances; return this; }
        public TripBalanceSummaryDTOBuilder netBalanceSum(BigDecimal netBalanceSum) { this.netBalanceSum = netBalanceSum; return this; }
        public TripBalanceSummaryDTOBuilder isBalanceValid(Boolean isBalanceValid) { this.isBalanceValid = isBalanceValid; return this; }

        public TripBalanceSummaryDTO build() {
            return new TripBalanceSummaryDTO(tripId, tripTitle, currency, totalSpending, balances, netBalanceSum, isBalanceValid);
        }
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getTripTitle() { return tripTitle; }
    public void setTripTitle(String tripTitle) { this.tripTitle = tripTitle; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getTotalSpending() { return totalSpending; }
    public void setTotalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; }

    public List<ParticipantBalanceDTO> getBalances() { return balances; }
    public void setBalances(List<ParticipantBalanceDTO> balances) { this.balances = balances; }

    public BigDecimal getNetBalanceSum() { return netBalanceSum; }
    public void setNetBalanceSum(BigDecimal netBalanceSum) { this.netBalanceSum = netBalanceSum; }

    public Boolean getIsBalanceValid() { return isBalanceValid; }
    public Boolean isBalanceValid() { return isBalanceValid; }
    public void setIsBalanceValid(Boolean isBalanceValid) { this.isBalanceValid = isBalanceValid; }
}
