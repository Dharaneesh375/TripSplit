package com.example.TripSplit.controller;

import com.example.TripSplit.dto.*;
import com.example.TripSplit.service.ExpenseService;
import com.example.TripSplit.service.SettlementService;
import com.example.TripSplit.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Trip Management", description = "Endpoints for creating and viewing trip details")
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;
    private final ExpenseService expenseService;
    private final SettlementService settlementService;

    public TripController(TripService tripService, ExpenseService expenseService, SettlementService settlementService) {
        this.tripService = tripService;
        this.expenseService = expenseService;
        this.settlementService = settlementService;
    }

    @PostMapping
    @Operation(summary = "1. Create a trip with participants", description = "Initialize a new trip and add participant members")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        TripResponse response = tripService.createTrip(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all trips", description = "Retrieve all created group trips")
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    @Operation(summary = "Get trip details by ID", description = "Retrieve trip meta info and participant list")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getTripResponseById(tripId));
    }

    @GetMapping("/{tripId}/summary")
    @Operation(summary = "5. View trip's full expense history and final settlement", description = "Complete consolidated view of trip expenses, net balances, and settlements")
    public ResponseEntity<TripSummaryDTO> getTripSummary(@PathVariable Long tripId) {
        TripResponse trip = tripService.getTripResponseById(tripId);
        TripBalanceSummaryDTO balanceSummary = expenseService.calculateParticipantBalances(tripId);
        List<ExpenseResponse> expenses = expenseService.getExpensesForTrip(tripId);
        List<SettlementDTO> settlements = settlementService.getSettlementsForTrip(tripId);

        TripSummaryDTO summary = TripSummaryDTO.builder()
                .trip(trip)
                .balanceSummary(balanceSummary)
                .expenses(expenses)
                .settlements(settlements)
                .build();

        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{tripId}")
    @Operation(summary = "Delete a trip", description = "Remove a trip and all associated data")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }
}
