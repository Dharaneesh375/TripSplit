package com.example.TripSplit.config;

import com.example.TripSplit.dto.CreateExpenseRequest;
import com.example.TripSplit.dto.CreateTripRequest;
import com.example.TripSplit.dto.TripResponse;
import com.example.TripSplit.service.ExpenseService;
import com.example.TripSplit.service.SettlementService;
import com.example.TripSplit.service.TripService;
import com.example.TripSplit.entity.Trip;
import com.example.TripSplit.entity.User;
import com.example.TripSplit.repository.TripRepository;
import com.example.TripSplit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TripService tripService;
    private final ExpenseService expenseService;
    private final SettlementService settlementService;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(TripService tripService,
                      ExpenseService expenseService,
                      SettlementService settlementService,
                      UserRepository userRepository,
                      TripRepository tripRepository,
                      PasswordEncoder passwordEncoder) {
        this.tripService = tripService;
        this.expenseService = expenseService;
        this.settlementService = settlementService;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed default demo user for instant login
        User demoUser = userRepository.findByEmail("demo@tripsplit.com").orElseGet(() -> {
            User user = User.builder()
                    .name("Demo User")
                    .email("demo@tripsplit.com")
                    .password(passwordEncoder.encode("Password123"))
                    .build();
            return userRepository.save(user);
        });

        // Seed initial sample trip for instant demo & evaluation
        CreateTripRequest tripReq = CreateTripRequest.builder()
                .title("Goa Beach Vacation 2026")
                .description("Annual getaway with college friends - Hotel, Fuel, Food & Water sports")
                .currency("INR")
                .participantNames(List.of("Alice", "Bob", "Charlie", "David"))
                .build();

        TripResponse trip = tripService.createTrip(tripReq);
        Long tripId = trip.getId();

        // Associate demo user with seeded trip
        Trip tripEntity = tripRepository.findById(tripId).orElse(null);
        if (tripEntity != null) {
            tripEntity.setUser(demoUser);
            tripRepository.save(tripEntity);
        }

        Long aliceId = trip.getParticipants().stream().filter(p -> p.getName().equals("Alice")).findFirst().get().getId();
        Long bobId = trip.getParticipants().stream().filter(p -> p.getName().equals("Bob")).findFirst().get().getId();
        Long charlieId = trip.getParticipants().stream().filter(p -> p.getName().equals("Charlie")).findFirst().get().getId();
        Long davidId = trip.getParticipants().stream().filter(p -> p.getName().equals("David")).findFirst().get().getId();

        // Expense 1: Alice pays for Villa Stay (₹12,000) shared by all 4
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Luxury Villa Stay (3 Nights)")
                .amount(new BigDecimal("12000.00"))
                .category("ACCOMMODATION")
                .payerId(aliceId)
                .participantIds(List.of(aliceId, bobId, charlieId, davidId))
                .build());

        // Expense 2: Bob pays for SUV Rental & Fuel (₹4,000) shared by all 4
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("SUV Car Rental & Petrol")
                .amount(new BigDecimal("4000.00"))
                .category("TRANSPORT")
                .payerId(bobId)
                .participantIds(List.of(aliceId, bobId, charlieId, davidId))
                .build());

        // Expense 3: Charlie pays for Seafood Dinner (₹2,400) shared by Alice, Bob, Charlie
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Beachside Seafood Dinner")
                .amount(new BigDecimal("2400.00"))
                .category("FOOD")
                .payerId(charlieId)
                .participantIds(List.of(aliceId, bobId, charlieId))
                .build());

        // Expense 4: David pays for Scuba Diving (₹3,000) shared by Charlie & David
        expenseService.addExpense(tripId, CreateExpenseRequest.builder()
                .description("Scuba Diving Adventure")
                .amount(new BigDecimal("3000.00"))
                .category("ENTERTAINMENT")
                .payerId(davidId)
                .participantIds(List.of(charlieId, davidId))
                .build());

        // Auto-generate initial minimal settlements
        settlementService.generateSettlements(tripId);
    }
}
