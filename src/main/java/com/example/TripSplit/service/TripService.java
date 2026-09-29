package com.example.TripSplit.service;

import com.example.TripSplit.dto.CreateTripRequest;
import com.example.TripSplit.dto.ParticipantDTO;
import com.example.TripSplit.dto.TripResponse;
import com.example.TripSplit.entity.Expense;
import com.example.TripSplit.entity.Participant;
import com.example.TripSplit.entity.Trip;
import com.example.TripSplit.entity.User;
import com.example.TripSplit.exception.ResourceNotFoundException;
import com.example.TripSplit.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final AuditLogService auditLogService;
    private final AuthService authService;

    public TripService(TripRepository tripRepository, AuditLogService auditLogService, AuthService authService) {
        this.tripRepository = tripRepository;
        this.auditLogService = auditLogService;
        this.authService = authService;
    }

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        User currentUser = authService.getAuthenticatedUser();

        Trip trip = Trip.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .currency(request.getCurrency() != null && !request.getCurrency().isBlank() ? request.getCurrency() : "INR")
                .user(currentUser)
                .build();

        if (request.getParticipantNames() != null) {
            for (String name : request.getParticipantNames()) {
                if (name != null && !name.isBlank()) {
                    Participant participant = Participant.builder()
                            .name(name.trim())
                            .trip(trip)
                            .build();
                    trip.getParticipants().add(participant);
                }
            }
        }

        Trip savedTrip = tripRepository.save(trip);
        String creatorInfo = currentUser != null ? " by " + currentUser.getName() : "";
        auditLogService.log(savedTrip.getId(), "TRIP_CREATED", "Created trip '" + savedTrip.getTitle() + "' with " + savedTrip.getParticipants().size() + " participants" + creatorInfo + ".");

        return mapToResponse(savedTrip);
    }

    @Transactional(readOnly = true)
    public TripResponse getTripResponseById(Long id) {
        Trip trip = getTripEntityById(id);
        return mapToResponse(trip);
    }

    @Transactional(readOnly = true)
    public Trip getTripEntityById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = getTripEntityById(id);
        tripRepository.delete(trip);
        auditLogService.log(id, "TRIP_DELETED", "Deleted trip with ID " + id);
    }

    public TripResponse mapToResponse(Trip trip) {
        List<ParticipantDTO> participantDTOs = trip.getParticipants().stream()
                .map(p -> ParticipantDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .email(p.getEmail())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalExpenses = trip.getExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TripResponse.TripResponseBuilder builder = TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .description(trip.getDescription())
                .currency(trip.getCurrency())
                .createdAt(trip.getCreatedAt())
                .participants(participantDTOs)
                .totalExpenses(totalExpenses)
                .expenseCount(trip.getExpenses().size());

        if (trip.getUser() != null) {
            builder.createdByUserId(trip.getUser().getId())
                    .createdByUserName(trip.getUser().getName())
                    .createdByUserEmail(trip.getUser().getEmail());
        }

        return builder.build();
    }
}
