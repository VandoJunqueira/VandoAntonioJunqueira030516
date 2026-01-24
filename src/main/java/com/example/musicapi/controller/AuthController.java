package com.example.musicapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.dto.AuthRequest;
import com.example.musicapi.dto.AuthResponse;
import com.example.musicapi.dto.RegisterRequest;
import com.example.musicapi.model.User;
import com.example.musicapi.repository.UserRepository;
import com.example.musicapi.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para Login e Registro")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e retornar JWT")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String jwt = tokenProvider.generateToken(authentication);
        long expiresIn = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();
        return ResponseEntity.ok(new AuthResponse(jwt, expiresIn));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        User.Role role = User.Role.USER;
        if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
            try {
                role = User.Role.valueOf(registerRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid role and keep default
            }
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Atualizar token JWT")
    public ResponseEntity<AuthResponse> refreshToken(Authentication authentication) {
        String jwt = tokenProvider.generateToken(authentication);
        long expiresIn = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();
        return ResponseEntity.ok(new AuthResponse(jwt, expiresIn));
    }
}
