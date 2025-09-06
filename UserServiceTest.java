package com.example.cmis.service;

import com.example.cmis.dto.*;
import com.example.cmis.exception.EmailAlreadyExistsException;
import com.example.cmis.model.User;
import com.example.cmis.repository.UserRepository;
import com.example.cmis.config.JwtUtil;
import com.example.cmis.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");
        req.setConfirmPassword("password");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashedPass");

        userService.registerUser(req);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_alreadyExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@example.com");
        req.setPassword("password");
        req.setConfirmPassword("password");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(req));
        assertEquals("User is already registered. Please login to the application.", ex.getMessage());
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("login@example.com");
        req.setPassword("password");

        User user = User.builder().email(req.getEmail()).passwordHash("hashedPass").role(Role.STUDENT).build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(req.getEmail())).thenReturn("token");

        var response = userService.login(req);
        assertNotNull(response);
        assertEquals("token", response.getToken());
    }
}