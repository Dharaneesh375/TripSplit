package com.example.TripSplit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateExpenseRequest {

    @NotBlank(message = "Expense description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String category;

    @NotNull(message = "Payer ID is required")
    private Long payerId;

    @NotEmpty(message = "At least one participant who shares the expense is required")
    private List<Long> participantIds;

    private Map<Long, BigDecimal> customSplits;

    public CreateExpenseRequest() {}

    public CreateExpenseRequest(String description, BigDecimal amount, String category, Long payerId, List<Long> participantIds, Map<Long, BigDecimal> customSplits) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.payerId = payerId;
        this.participantIds = participantIds;
        this.customSplits = customSplits;
    }

    public static CreateExpenseRequestBuilder builder() {
        return new CreateExpenseRequestBuilder();
    }

    public static class CreateExpenseRequestBuilder {
        private String description;
        private BigDecimal amount;
        private String category;
        private Long payerId;
        private List<Long> participantIds;
        private Map<Long, BigDecimal> customSplits;

        public CreateExpenseRequestBuilder description(String description) { this.description = description; return this; }
        public CreateExpenseRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public CreateExpenseRequestBuilder category(String category) { this.category = category; return this; }
        public CreateExpenseRequestBuilder payerId(Long payerId) { this.payerId = payerId; return this; }
        public CreateExpenseRequestBuilder participantIds(List<Long> participantIds) { this.participantIds = participantIds; return this; }
        public CreateExpenseRequestBuilder customSplits(Map<Long, BigDecimal> customSplits) { this.customSplits = customSplits; return this; }

        public CreateExpenseRequest build() {
            return new CreateExpenseRequest(description, amount, category, payerId, participantIds, customSplits);
        }
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getPayerId() { return payerId; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }

    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }

    public Map<Long, BigDecimal> getCustomSplits() { return customSplits; }
    public void setCustomSplits(Map<Long, BigDecimal> customSplits) { this.customSplits = customSplits; }
}
