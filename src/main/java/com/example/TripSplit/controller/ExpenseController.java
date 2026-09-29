package com.example.TripSplit.controller;

import com.example.TripSplit.dto.*;
import com.example.TripSplit.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}")
@Tag(name = "Expense & Balance Management", description = "Endpoints for logging shared expenses and computing net balances")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    @Operation(summary = "2. Log an expense", description = "Log an expense with payer, amount, category, and shared participants (supports equal or custom splits)")
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse response = expenseService.addExpense(tripId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    @Operation(summary = "View expense history (with optional pagination & sorting)", description = "Fetch expense history for a trip")
    public ResponseEntity<?> getExpenses(
            @PathVariable Long tripId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction) {

        if (page != null) {
            Sort.Direction sortDir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
            Page<ExpenseResponse> paginated = expenseService.getExpensesForTripPaginated(tripId, pageRequest);
            return ResponseEntity.ok(paginated);
        }

        List<ExpenseResponse> expenses = expenseService.getExpensesForTrip(tripId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/balances")
    @Operation(summary = "3. Compute each participant's net balance", description = "Calculates paid minus owed share for all participants and validates zero sum balance rule")
    public ResponseEntity<TripBalanceSummaryDTO> getBalances(@PathVariable Long tripId) {
        TripBalanceSummaryDTO summary = expenseService.calculateParticipantBalances(tripId);
        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/expenses/{expenseId}")
    @Operation(summary = "Delete an expense", description = "Delete an expense record by ID")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long tripId,
            @PathVariable Long expenseId) {
        expenseService.deleteExpense(tripId, expenseId);
        return ResponseEntity.noContent().build();
    }
}
