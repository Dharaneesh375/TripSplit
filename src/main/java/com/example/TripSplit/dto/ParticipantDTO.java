package com.example.TripSplit.dto;

public class ParticipantDTO {
    private Long id;
    private String name;
    private String email;

    public ParticipantDTO() {}

    public ParticipantDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static ParticipantDTOBuilder builder() {
        return new ParticipantDTOBuilder();
    }

    public static class ParticipantDTOBuilder {
        private Long id;
        private String name;
        private String email;

        public ParticipantDTOBuilder id(Long id) { this.id = id; return this; }
        public ParticipantDTOBuilder name(String name) { this.name = name; return this; }
        public ParticipantDTOBuilder email(String email) { this.email = email; return this; }

        public ParticipantDTO build() {
            return new ParticipantDTO(id, name, email);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
