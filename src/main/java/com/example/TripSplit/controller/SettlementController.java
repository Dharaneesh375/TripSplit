package com.example.TripSplit.controller;

import com.example.TripSplit.dto.SettlementDTO;
import com.example.TripSplit.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/settlements")
@Tag(name = "Settlement Optimization", description = "Endpoints for generating minimal settlement transactions and recording payments")
@CrossOrigin(origins = "*")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/generate")
    @Operation(summary = "4. Generate a minimal set of settlement transactions", description = "Executes debt minimization algorithm to compute minimum transactions clearing all balances to zero")
    public ResponseEntity<List<SettlementDTO>> generateSettlements(@PathVariable Long tripId) {
        List<SettlementDTO> settlements = settlementService.generateSettlements(tripId);
        return ResponseEntity.ok(settlements);
    }

    @GetMapping
    @Operation(summary = "Get generated settlement transactions", description = "Retrieve generated settlements for a trip")
    public ResponseEntity<List<SettlementDTO>> getSettlements(@PathVariable Long tripId) {
        List<SettlementDTO> settlements = settlementService.getSettlementsForTrip(tripId);
        return ResponseEntity.ok(settlements);
    }

    @PatchMapping("/{settlementId}/settle")
    @Operation(summary = "Mark settlement transaction as paid", description = "Mark a specific debt transaction as paid/settled")
    public ResponseEntity<SettlementDTO> markSettled(
            @PathVariable Long tripId,
            @PathVariable Long settlementId) {
        SettlementDTO updated = settlementService.markSettled(tripId, settlementId);
        return ResponseEntity.ok(updated);
    }
}
