package com.example.TripSplit.service;

import com.example.TripSplit.dto.AuthResponse;
import com.example.TripSplit.dto.LoginRequest;
import com.example.TripSplit.dto.RegisterRequest;
import com.example.TripSplit.dto.UserDTO;
import com.example.TripSplit.entity.User;
import com.example.TripSplit.exception.BusinessRuleViolationException;
import com.example.TripSplit.exception.DuplicateEmailException;
import com.example.TripSplit.exception.ResourceNotFoundException;
import com.example.TripSplit.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessRuleViolationException("Password and confirm password do not match");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("An account with email '" + normalizedEmail + "' already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateToken(savedUser.getEmail());

        UserDTO userDTO = UserDTO.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .user(userDTO)
                .message("User registered successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));

        String jwtToken = jwtService.generateToken(user.getEmail());

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .user(userDTO)
                .message("Login successful")
                .build();
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email.toLowerCase()).orElse(null);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUserDTO() {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new ResourceNotFoundException("No authenticated user found");
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
