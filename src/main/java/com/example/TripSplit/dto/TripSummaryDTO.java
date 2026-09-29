package com.example.TripSplit.dto;

import java.util.List;

public class TripSummaryDTO {
    private TripResponse trip;
    private TripBalanceSummaryDTO balanceSummary;
    private List<ExpenseResponse> expenses;
    private List<SettlementDTO> settlements;

    public TripSummaryDTO() {}

    public TripSummaryDTO(TripResponse trip, TripBalanceSummaryDTO balanceSummary, List<ExpenseResponse> expenses, List<SettlementDTO> settlements) {
        this.trip = trip;
        this.balanceSummary = balanceSummary;
        this.expenses = expenses;
        this.settlements = settlements;
    }

    public static TripSummaryDTOBuilder builder() {
        return new TripSummaryDTOBuilder();
    }

    public static class TripSummaryDTOBuilder {
        private TripResponse trip;
        private TripBalanceSummaryDTO balanceSummary;
        private List<ExpenseResponse> expenses;
        private List<SettlementDTO> settlements;

        public TripSummaryDTOBuilder trip(TripResponse trip) { this.trip = trip; return this; }
        public TripSummaryDTOBuilder balanceSummary(TripBalanceSummaryDTO balanceSummary) { this.balanceSummary = balanceSummary; return this; }
        public TripSummaryDTOBuilder expenses(List<ExpenseResponse> expenses) { this.expenses = expenses; return this; }
        public TripSummaryDTOBuilder settlements(List<SettlementDTO> settlements) { this.settlements = settlements; return this; }

        public TripSummaryDTO build() {
            return new TripSummaryDTO(trip, balanceSummary, expenses, settlements);
        }
    }

    public TripResponse getTrip() { return trip; }
    public void setTrip(TripResponse trip) { this.trip = trip; }

    public TripBalanceSummaryDTO getBalanceSummary() { return balanceSummary; }
    public void setBalanceSummary(TripBalanceSummaryDTO balanceSummary) { this.balanceSummary = balanceSummary; }

    public List<ExpenseResponse> getExpenses() { return expenses; }
    public void setExpenses(List<ExpenseResponse> expenses) { this.expenses = expenses; }

    public List<SettlementDTO> getSettlements() { return settlements; }
    public void setSettlements(List<SettlementDTO> settlements) { this.settlements = settlements; }
}
