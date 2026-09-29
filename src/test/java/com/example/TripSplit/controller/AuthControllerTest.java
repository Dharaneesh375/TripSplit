package com.example.TripSplit.controller;

import com.example.TripSplit.dto.CreateTripRequest;
import com.example.TripSplit.dto.LoginRequest;
import com.example.TripSplit.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created and JWT token")
    public void testApiRegisterSuccess() throws Exception {
        String email = "api_user_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest request = RegisterRequest.builder()
                .name("API User")
                .email(email)
                .password("Password123")
                .confirmPassword("Password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value(email.toLowerCase()));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK and JWT token")
    public void testApiLoginSuccess() throws Exception {
        String email = "login_api_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest regRequest = RegisterRequest.builder()
                .name("Login User")
                .email(email)
                .password("SecurePass999")
                .confirmPassword("SecurePass999")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password("SecurePass999")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email.toLowerCase()));
    }

    @Test
    @DisplayName("Accessing protected endpoint /api/trips without token should return 401 Unauthorized")
    public void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Accessing protected endpoint with valid Bearer token should succeed")
    public void testProtectedEndpointWithToken() throws Exception {
        String email = "bearer_user_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest regRequest = RegisterRequest.builder()
                .name("Bearer User")
                .email(email)
                .password("Pass123456")
                .confirmPassword("Pass123456")
                .build();

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // Call protected GET /api/trips with Bearer token
        mockMvc.perform(get("/api/trips")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Create a trip with Bearer token
        CreateTripRequest tripRequest = CreateTripRequest.builder()
                .title("Weekend Road Trip")
                .description("Mountain cabin trip")
                .currency("INR")
                .participantNames(List.of("Alice", "Bob"))
                .build();

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Weekend Road Trip"))
                .andExpect(jsonPath("$.createdByUserName").value("Bearer User"));
    }
}
