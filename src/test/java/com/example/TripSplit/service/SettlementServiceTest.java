package com.example.TripSplit.service;

import com.example.TripSplit.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SettlementServiceTest {

    @Autowired
    private TripService tripService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SettlementService settlementService;

    @Test
    @DisplayName("Test Business Rule 1: Sum of all participants' net balances for a trip must always equal zero")
    public void testNetBalancesSumToZero() {
        CreateTripRequest tripReq = CreateTripRequest.builder()
                .title("Test Himalayan Trip")
                .currency("INR")
                .participantNames(List.of("Alice", "Bob", "Charlie"))
                .build();

        TripResponse trip = tripService.createTrip(tripReq);
        Long tripId = trip.getId();

        Long aliceId = trip.getParticipants().stream().filter(p -> p.getName().equals("Alice")).findFirst().get().getId();
        Long bobId = trip.getParticipants().stream().filter(p -> p.getName().equals("Bob")).findFirst().get().getId();
        Long charlieId = trip.getParticipants().stream().filter(p -> p.getName().equals("Charlie")).findFirst().get().getId();

        // Alice pays 300 for all 3
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Hotel")
                .amount(new BigDecimal("300.00"))
                .payerId(aliceId)
                .participantIds(List.of(aliceId, bobId, charlieId))
                .build());

        // Bob pays 90 for Bob and Charlie
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Food")
                .amount(new BigDecimal("90.00"))
                .payerId(bobId)
                .participantIds(List.of(bobId, charlieId))
                .build());

        TripBalanceSummaryDTO balanceSummary = expenseService.calculateParticipantBalances(tripId);

        assertTrue(balanceSummary.getIsBalanceValid(), "Net balance sum must be zero");
        assertEquals(new BigDecimal("0.00"), balanceSummary.getNetBalanceSum());
    }

    @Test
    @DisplayName("Test Business Rule 2: Minimal settlement generation clears all participant balances to zero")
    public void testSettlementClearsBalancesToZero() {
        CreateTripRequest tripReq = CreateTripRequest.builder()
                .title("Test Goa Vacation")
                .currency("INR")
                .participantNames(List.of("Alice", "Bob", "Charlie", "David"))
                .build();

        TripResponse trip = tripService.createTrip(tripReq);
        Long tripId = trip.getId();

        Long aliceId = trip.getParticipants().stream().filter(p -> p.getName().equals("Alice")).findFirst().get().getId();
        Long bobId = trip.getParticipants().stream().filter(p -> p.getName().equals("Bob")).findFirst().get().getId();
        Long charlieId = trip.getParticipants().stream().filter(p -> p.getName().equals("Charlie")).findFirst().get().getId();
        Long davidId = trip.getParticipants().stream().filter(p -> p.getName().equals("David")).findFirst().get().getId();

        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Resort")
                .amount(new BigDecimal("400.00"))
                .payerId(aliceId)
                .participantIds(List.of(aliceId, bobId, charlieId, davidId))
                .build());

        List<SettlementDTO> settlements = settlementService.generateSettlements(tripId);

        assertNotNull(settlements);
        assertFalse(settlements.isEmpty(), "Settlements should be generated");

        // Verify total settlement amount equals total net debts
        BigDecimal totalSettledAmount = settlements.stream()
                .map(SettlementDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Alice paid 400, share was 100 -> owed 300. Bob, Charlie, David owe 100 each.
        // Total transfers should be 300.
        assertEquals(new BigDecimal("300.00"), totalSettledAmount);
    }
}
