package com.example.TripSplit.service;

import com.example.TripSplit.dto.*;
import com.example.TripSplit.entity.*;
import com.example.TripSplit.exception.BusinessRuleViolationException;
import com.example.TripSplit.exception.ResourceNotFoundException;
import com.example.TripSplit.repository.ExpenseRepository;
import com.example.TripSplit.repository.ParticipantRepository;
import com.example.TripSplit.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ExpenseService {

    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;
    private final AuditLogService auditLogService;

    public ExpenseService(TripRepository tripRepository, ParticipantRepository participantRepository,
            ExpenseRepository expenseRepository, AuditLogService auditLogService) {
        this.tripRepository = tripRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ExpenseResponse addExpense(Long tripId, CreateExpenseRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        Participant payer = participantRepository.findById(request.getPayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found with id: " + request.getPayerId()));

        if (!payer.getTrip().getId().equals(tripId)) {
            throw new BusinessRuleViolationException(
                    "Payer with ID " + payer.getId() + " does not belong to trip ID " + tripId);
        }

        List<Participant> sharingParticipants = participantRepository.findAllById(request.getParticipantIds());
        if (sharingParticipants.size() != request.getParticipantIds().size()) {
            throw new ResourceNotFoundException("One or more participant IDs were not found");
        }

        for (Participant p : sharingParticipants) {
            if (!p.getTrip().getId().equals(tripId)) {
                throw new BusinessRuleViolationException(
                        "Participant " + p.getName() + " (ID " + p.getId() + ") does not belong to trip ID " + tripId);
            }
        }

        Expense expense = Expense.builder()
                .trip(trip)
                .payer(payer)
                .description(request.getDescription())
                .amount(request.getAmount().setScale(2, RoundingMode.HALF_UP))
                .category(request.getCategory() != null && !request.getCategory().isBlank() ? request.getCategory()
                        : "GENERAL")
                .splits(new ArrayList<>())
                .build();

        BigDecimal totalAmount = expense.getAmount();

        if (request.getCustomSplits() != null && !request.getCustomSplits().isEmpty()) {
            // Custom splits validation
            BigDecimal customSum = BigDecimal.ZERO;
            for (Participant p : sharingParticipants) {
                BigDecimal share = request.getCustomSplits().get(p.getId());
                if (share == null) {
                    throw new BusinessRuleViolationException("Custom split missing for participant ID: " + p.getId());
                }
                customSum = customSum.add(share);
            }
            customSum = customSum.setScale(2, RoundingMode.HALF_UP);

            if (customSum.compareTo(totalAmount) != 0) {
                throw new BusinessRuleViolationException(
                        String.format("Sum of custom splits (%.2f) must equal expense total amount (%.2f)", customSum,
                                totalAmount));
            }

            for (Participant p : sharingParticipants) {
                BigDecimal share = request.getCustomSplits().get(p.getId()).setScale(2, RoundingMode.HALF_UP);
                ExpenseSplit split = ExpenseSplit.builder()
                        .expense(expense)
                        .participant(p)
                        .owedAmount(share)
                        .build();
                expense.getSplits().add(split);
            }
        } else {
            // Equal split calculation with penny rounding distribution
            int count = sharingParticipants.size();
            BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
            BigDecimal remainder = totalAmount.subtract(baseShare.multiply(BigDecimal.valueOf(count)));

            for (int i = 0; i < count; i++) {
                Participant p = sharingParticipants.get(i);
                BigDecimal share = baseShare;
                // Add 1 penny to first 'remainder' participants so sum exactly equals
                // totalAmount
                if (i < remainder.multiply(BigDecimal.valueOf(100)).intValue()) {
                    share = share.add(new BigDecimal("0.01"));
                }

                ExpenseSplit split = ExpenseSplit.builder()
                        .expense(expense)
                        .participant(p)
                        .owedAmount(share)
                        .build();
                expense.getSplits().add(split);
            }
        }

        Expense savedExpense = expenseRepository.save(expense);

        // Enforce Business Rule 1: Sum of participants' net balances for trip must
        // equal zero
        TripBalanceSummaryDTO balanceSummary = calculateParticipantBalances(tripId);
        if (!balanceSummary.getIsBalanceValid()) {
            throw new BusinessRuleViolationException(
                    "Business Rule Violation: Sum of participants' net balances is non-zero ("
                            + balanceSummary.getNetBalanceSum() + ")");
        }

        auditLogService.log(tripId, "EXPENSE_ADDED", String.format("Added expense '%s' of %s %.2f paid by %s",
                savedExpense.getDescription(), trip.getCurrency(), savedExpense.getAmount(), payer.getName()));

        return mapToExpenseResponse(savedExpense);
    }

    @Transactional(readOnly = true)
    public TripBalanceSummaryDTO calculateParticipantBalances(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        List<Participant> participants = participantRepository.findByTripId(tripId);
        List<Expense> expenses = expenseRepository.findByTripId(tripId);

        Map<Long, BigDecimal> totalPaidMap = new HashMap<>();
        Map<Long, BigDecimal> totalOwedMap = new HashMap<>();

        for (Participant p : participants) {
            totalPaidMap.put(p.getId(), BigDecimal.ZERO);
            totalOwedMap.put(p.getId(), BigDecimal.ZERO);
        }

        BigDecimal totalSpending = BigDecimal.ZERO;

        for (Expense exp : expenses) {
            totalSpending = totalSpending.add(exp.getAmount());
            Long payerId = exp.getPayer().getId();
            totalPaidMap.put(payerId, totalPaidMap.getOrDefault(payerId, BigDecimal.ZERO).add(exp.getAmount()));

            for (ExpenseSplit split : exp.getSplits()) {
                Long partId = split.getParticipant().getId();
                totalOwedMap.put(partId, totalOwedMap.getOrDefault(partId, BigDecimal.ZERO).add(split.getOwedAmount()));
            }
        }

        List<ParticipantBalanceDTO> balanceList = new ArrayList<>();
        BigDecimal sumNetBalances = BigDecimal.ZERO;

        for (Participant p : participants) {
            BigDecimal paid = totalPaidMap.getOrDefault(p.getId(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal owed = totalOwedMap.getOrDefault(p.getId(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal net = paid.subtract(owed).setScale(2, RoundingMode.HALF_UP);

            sumNetBalances = sumNetBalances.add(net);

            balanceList.add(ParticipantBalanceDTO.builder()
                    .participantId(p.getId())
                    .participantName(p.getName())
                    .totalPaid(paid)
                    .totalOwed(owed)
                    .netBalance(net)
                    .build());
        }

        sumNetBalances = sumNetBalances.setScale(2, RoundingMode.HALF_UP);
        boolean isBalanced = sumNetBalances.compareTo(BigDecimal.ZERO) == 0;

        return TripBalanceSummaryDTO.builder()
                .tripId(trip.getId())
                .tripTitle(trip.getTitle())
                .currency(trip.getCurrency())
                .totalSpending(totalSpending.setScale(2, RoundingMode.HALF_UP))
                .balances(balanceList)
                .netBalanceSum(sumNetBalances)
                .isBalanceValid(isBalanced)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesForTrip(Long tripId) {
        List<Expense> expenses = expenseRepository.findByTripId(tripId);
        List<ExpenseResponse> responses = new ArrayList<>();
        for (Expense e : expenses) {
            responses.add(mapToExpenseResponse(e));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesForTripPaginated(Long tripId, Pageable pageable) {
        Page<Expense> expensePage = expenseRepository.findByTripId(tripId, pageable);
        return expensePage.map(this::mapToExpenseResponse);
    }

    @Transactional
    public void deleteExpense(Long tripId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        if (!expense.getTrip().getId().equals(tripId)) {
            throw new BusinessRuleViolationException("Expense does not belong to trip ID: " + tripId);
        }

        expenseRepository.delete(expense);
        auditLogService.log(tripId, "EXPENSE_DELETED",
                "Deleted expense '" + expense.getDescription() + "' (ID " + expenseId + ")");
    }

    public ExpenseResponse mapToExpenseResponse(Expense expense) {
        List<ExpenseSplitDTO> splitDTOs = new ArrayList<>();
        for (ExpenseSplit s : expense.getSplits()) {
            splitDTOs.add(ExpenseSplitDTO.builder()
                    .participantId(s.getParticipant().getId())
                    .participantName(s.getParticipant().getName())
                    .owedAmount(s.getOwedAmount())
                    .build());
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .payerId(expense.getPayer().getId())
                .payerName(expense.getPayer().getName())
                .createdAt(expense.getCreatedAt())
                .splits(splitDTOs)
                .build();
    }
}
