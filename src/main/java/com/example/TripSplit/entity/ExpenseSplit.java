package com.example.TripSplit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @JsonIgnore
    private Expense expense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(name = "owed_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal owedAmount;

    public ExpenseSplit() {}

    public ExpenseSplit(Long id, Expense expense, Participant participant, BigDecimal owedAmount) {
        this.id = id;
        this.expense = expense;
        this.participant = participant;
        this.owedAmount = owedAmount;
    }

    public static ExpenseSplitBuilder builder() {
        return new ExpenseSplitBuilder();
    }

    public static class ExpenseSplitBuilder {
        private Long id;
        private Expense expense;
        private Participant participant;
        private BigDecimal owedAmount;

        public ExpenseSplitBuilder id(Long id) { this.id = id; return this; }
        public ExpenseSplitBuilder expense(Expense expense) { this.expense = expense; return this; }
        public ExpenseSplitBuilder participant(Participant participant) { this.participant = participant; return this; }
        public ExpenseSplitBuilder owedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; return this; }

        public ExpenseSplit build() {
            return new ExpenseSplit(id, expense, participant, owedAmount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }

    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }

    public BigDecimal getOwedAmount() { return owedAmount; }
    public void setOwedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; }
}
