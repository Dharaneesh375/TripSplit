package com.example.TripSplit.service;

import com.example.TripSplit.dto.*;
import com.example.TripSplit.entity.Participant;
import com.example.TripSplit.entity.Settlement;
import com.example.TripSplit.entity.Trip;
import com.example.TripSplit.exception.BusinessRuleViolationException;
import com.example.TripSplit.exception.ResourceNotFoundException;
import com.example.TripSplit.repository.ParticipantRepository;
import com.example.TripSplit.repository.SettlementRepository;
import com.example.TripSplit.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;
    private final SettlementRepository settlementRepository;
    private final ExpenseService expenseService;
    private final AuditLogService auditLogService;

    public SettlementService(TripRepository tripRepository, ParticipantRepository participantRepository, SettlementRepository settlementRepository, ExpenseService expenseService, AuditLogService auditLogService) {
        this.tripRepository = tripRepository;
        this.participantRepository = participantRepository;
        this.settlementRepository = settlementRepository;
        this.expenseService = expenseService;
        this.auditLogService = auditLogService;
    }

    private static class DebtNode {
        Long participantId;
        String participantName;
        BigDecimal balance; // positive for creditor, negative for debtor

        DebtNode(Long participantId, String participantName, BigDecimal balance) {
            this.participantId = participantId;
            this.participantName = participantName;
            this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Transactional
    public List<SettlementDTO> generateSettlements(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Get current participant balances
        TripBalanceSummaryDTO balanceSummary = expenseService.calculateParticipantBalances(tripId);

        if (!balanceSummary.getIsBalanceValid()) {
            throw new BusinessRuleViolationException(
                    "Cannot generate settlements: Participant net balances for trip do not sum to zero (Sum = "
                            + balanceSummary.getNetBalanceSum() + ")"
            );
        }

        // Delete existing un-executed settlements for fresh minimal generation
        settlementRepository.deleteByTripId(tripId);

        List<DebtNode> debtors = new ArrayList<>();   // net balance < 0
        List<DebtNode> creditors = new ArrayList<>(); // net balance > 0

        Map<Long, BigDecimal> initialBalances = new HashMap<>();

        for (ParticipantBalanceDTO pb : balanceSummary.getBalances()) {
            initialBalances.put(pb.getParticipantId(), pb.getNetBalance());

            if (pb.getNetBalance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new DebtNode(pb.getParticipantId(), pb.getParticipantName(), pb.getNetBalance().abs()));
            } else if (pb.getNetBalance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new DebtNode(pb.getParticipantId(), pb.getParticipantName(), pb.getNetBalance()));
            }
        }

        // Sort debtors and creditors descending by balance for optimal greedy settlement
        debtors.sort((a, b) -> b.balance.compareTo(a.balance));
        creditors.sort((a, b) -> b.balance.compareTo(a.balance));

        List<Settlement> generatedSettlements = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            DebtNode debtor = debtors.get(i);
            DebtNode creditor = creditors.get(j);

            BigDecimal transferAmount = debtor.balance.min(creditor.balance).setScale(2, RoundingMode.HALF_UP);

            if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                Participant fromP = participantRepository.findById(debtor.participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Participant not found: " + debtor.participantId));
                Participant toP = participantRepository.findById(creditor.participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Participant not found: " + creditor.participantId));

                Settlement settlement = Settlement.builder()
                        .trip(trip)
                        .fromParticipant(fromP)
                        .toParticipant(toP)
                        .amount(transferAmount)
                        .isSettled(false)
                        .build();

                generatedSettlements.add(settlement);

                debtor.balance = debtor.balance.subtract(transferAmount).setScale(2, RoundingMode.HALF_UP);
                creditor.balance = creditor.balance.subtract(transferAmount).setScale(2, RoundingMode.HALF_UP);
            }

            if (debtor.balance.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (creditor.balance.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        // Verify Business Rule 2: Settlement transactions generated must fully clear every participant's balance to zero
        Map<Long, BigDecimal> postSettlementBalances = new HashMap<>(initialBalances);
        for (Settlement s : generatedSettlements) {
            Long debtorId = s.getFromParticipant().getId();
            Long creditorId = s.getToParticipant().getId();
            BigDecimal amt = s.getAmount();

            postSettlementBalances.put(debtorId, postSettlementBalances.get(debtorId).add(amt));
            postSettlementBalances.put(creditorId, postSettlementBalances.get(creditorId).subtract(amt));
        }

        for (Map.Entry<Long, BigDecimal> entry : postSettlementBalances.entrySet()) {
            if (entry.getValue().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) != 0) {
                throw new BusinessRuleViolationException(
                        "Business Rule Violation: Settlement calculation failed to clear balance to zero for participant ID " + entry.getKey()
                );
            }
        }

        List<Settlement> savedSettlements = settlementRepository.saveAll(generatedSettlements);

        auditLogService.log(tripId, "SETTLEMENTS_GENERATED",
                String.format("Generated %d minimal settlement transactions to fully clear all balances.", savedSettlements.size()));

        return savedSettlements.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SettlementDTO> getSettlementsForTrip(Long tripId) {
        return settlementRepository.findByTripId(tripId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SettlementDTO markSettled(Long tripId, Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found with id: " + settlementId));

        if (!settlement.getTrip().getId().equals(tripId)) {
            throw new BusinessRuleViolationException("Settlement does not belong to trip ID: " + tripId);
        }

        settlement.setIsSettled(true);
        Settlement updated = settlementRepository.save(settlement);

        auditLogService.log(tripId, "SETTLEMENT_PAID",
                String.format("%s paid %s %.2f to %s",
                        settlement.getFromParticipant().getName(),
                        settlement.getTrip().getCurrency(),
                        settlement.getAmount(),
                        settlement.getToParticipant().getName()));

        return mapToDTO(updated);
    }

    public SettlementDTO mapToDTO(Settlement s) {
        return SettlementDTO.builder()
                .id(s.getId())
                .fromParticipantId(s.getFromParticipant().getId())
                .fromParticipantName(s.getFromParticipant().getName())
                .toParticipantId(s.getToParticipant().getId())
                .toParticipantName(s.getToParticipant().getName())
                .amount(s.getAmount())
                .isSettled(s.getIsSettled())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
