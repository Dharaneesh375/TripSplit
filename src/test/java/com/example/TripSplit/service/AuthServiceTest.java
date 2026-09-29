package com.example.TripSplit.service;

import com.example.TripSplit.dto.AuthResponse;
import com.example.TripSplit.dto.LoginRequest;
import com.example.TripSplit.dto.RegisterRequest;
import com.example.TripSplit.exception.BusinessRuleViolationException;
import com.example.TripSplit.exception.DuplicateEmailException;
import com.example.TripSplit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should successfully register a new user and return JWT token")
    public void testSuccessfulRegistration() {
        String email = "testuser_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Test User")
                .email(email)
                .password("SecurePass123")
                .confirmPassword("SecurePass123")
                .build();

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("Test User", response.getUser().getName());
        assertEquals(email.toLowerCase(), response.getUser().getEmail());
        assertTrue(userRepository.existsByEmail(email.toLowerCase()));
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when registering an already registered email")
    public void testDuplicateEmailRegistration() {
        String email = "duplicate_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest req1 = RegisterRequest.builder()
                .name("First User")
                .email(email)
                .password("Password123")
                .confirmPassword("Password123")
                .build();
        authService.register(req1);

        RegisterRequest req2 = RegisterRequest.builder()
                .name("Second User")
                .email(email)
                .password("Password456")
                .confirmPassword("Password456")
                .build();

        assertThrows(DuplicateEmailException.class, () -> authService.register(req2));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when confirm password does not match")
    public void testPasswordMismatchRegistration() {
        String email = "mismatch_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Mismatch User")
                .email(email)
                .password("Password123")
                .confirmPassword("DifferentPassword")
                .build();

        assertThrows(BusinessRuleViolationException.class, () -> authService.register(req));
    }

    @Test
    @DisplayName("Should successfully log in with valid credentials and return JWT token")
    public void testSuccessfulLogin() {
        String email = "login_success_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest regReq = RegisterRequest.builder()
                .name("Login User")
                .email(email)
                .password("ValidPass123")
                .confirmPassword("ValidPass123")
                .build();
        authService.register(regReq);

        LoginRequest loginReq = LoginRequest.builder()
                .email(email)
                .password("ValidPass123")
                .build();

        AuthResponse loginResponse = authService.login(loginReq);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getTokenType());
        assertEquals(email.toLowerCase(), loginResponse.getUser().getEmail());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid password")
    public void testInvalidPasswordLogin() {
        String email = "wrongpass_" + System.currentTimeMillis() + "@tripsplit.com";
        RegisterRequest regReq = RegisterRequest.builder()
                .name("Wrong Pass User")
                .email(email)
                .password("CorrectPass123")
                .confirmPassword("CorrectPass123")
                .build();
        authService.register(regReq);

        LoginRequest loginReq = LoginRequest.builder()
                .email(email)
                .password("IncorrectPassword")
                .build();

        assertThrows(BadCredentialsException.class, () -> authService.login(loginReq));
    }
}
