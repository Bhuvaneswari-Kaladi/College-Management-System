package com.example.cmis.service;

import com.example.cmis.dto.LoginRequest;
import com.example.cmis.dto.LoginResponse;
import com.example.cmis.dto.RegisterRequest;
import com.example.cmis.exception.EmailAlreadyExistsException;
import com.example.cmis.exception.UserNotFoundException;
import com.example.cmis.model.User;
import com.example.cmis.repository.UserRepository;
import com.example.cmis.util.Role;
import com.example.cmis.config.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("User is already registered. Please login to the application.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT) // Default role as STUDENT, modify as required
                .build();
        userRepository.save(user);
        logger.info("New user registered with email: {}", request.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));
            String token = jwtUtil.generateToken(user.getEmail());
            logger.info("User logged in successfully: {}", request.getEmail());
            return new LoginResponse(token);
        } catch (BadCredentialsException ex) {
            logger.warn("Failed login attempt for email: {}", request.getEmail());
            throw ex;
        }
    }
}