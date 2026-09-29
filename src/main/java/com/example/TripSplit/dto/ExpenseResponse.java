package com.example.TripSplit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String category;
    private Long payerId;
    private String payerName;
    private LocalDateTime createdAt;
    private List<ExpenseSplitDTO> splits;

    public ExpenseResponse() {}

    public ExpenseResponse(Long id, String description, BigDecimal amount, String category, Long payerId, String payerName, LocalDateTime createdAt, List<ExpenseSplitDTO> splits) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.payerId = payerId;
        this.payerName = payerName;
        this.createdAt = createdAt;
        this.splits = splits;
    }

    public static ExpenseResponseBuilder builder() {
        return new ExpenseResponseBuilder();
    }

    public static class ExpenseResponseBuilder {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String category;
        private Long payerId;
        private String payerName;
        private LocalDateTime createdAt;
        private List<ExpenseSplitDTO> splits;

        public ExpenseResponseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseResponseBuilder description(String description) { this.description = description; return this; }
        public ExpenseResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseResponseBuilder category(String category) { this.category = category; return this; }
        public ExpenseResponseBuilder payerId(Long payerId) { this.payerId = payerId; return this; }
        public ExpenseResponseBuilder payerName(String payerName) { this.payerName = payerName; return this; }
        public ExpenseResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ExpenseResponseBuilder splits(List<ExpenseSplitDTO> splits) { this.splits = splits; return this; }

        public ExpenseResponse build() {
            return new ExpenseResponse(id, description, amount, category, payerId, payerName, createdAt, splits);
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

    public Long getPayerId() { return payerId; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ExpenseSplitDTO> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplitDTO> splits) { this.splits = splits; }
}
