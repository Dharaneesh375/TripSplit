package com.example.TripSplit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateTripRequest {

    @NotBlank(message = "Trip title cannot be blank")
    private String title;

    private String description;

    private String currency;

    @NotEmpty(message = "At least one participant is required")
    private List<String> participantNames;

    public CreateTripRequest() {}

    public CreateTripRequest(String title, String description, String currency, List<String> participantNames) {
        this.title = title;
        this.description = description;
        this.currency = currency;
        this.participantNames = participantNames;
    }

    public static CreateTripRequestBuilder builder() {
        return new CreateTripRequestBuilder();
    }

    public static class CreateTripRequestBuilder {
        private String title;
        private String description;
        private String currency;
        private List<String> participantNames;

        public CreateTripRequestBuilder title(String title) { this.title = title; return this; }
        public CreateTripRequestBuilder description(String description) { this.description = description; return this; }
        public CreateTripRequestBuilder currency(String currency) { this.currency = currency; return this; }
        public CreateTripRequestBuilder participantNames(List<String> participantNames) { this.participantNames = participantNames; return this; }

        public CreateTripRequest build() {
            return new CreateTripRequest(title, description, currency, participantNames);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<String> getParticipantNames() { return participantNames; }
    public void setParticipantNames(List<String> participantNames) { this.participantNames = participantNames; }
}
