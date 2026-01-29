package com.example.musicapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.dto.ApiResponse;
import com.example.musicapi.dto.AuthRequest;
import com.example.musicapi.dto.AuthResponse;
import com.example.musicapi.dto.RegisterRequest;
import com.example.musicapi.model.User;
import com.example.musicapi.repository.UserRepository;
import com.example.musicapi.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            String jwt = tokenProvider.generateToken(authentication);
            long expiresIn = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();

            // Buscar dados do usuário
            com.example.musicapi.model.User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));
            com.example.musicapi.dto.UserResponse userResponse = new com.example.musicapi.dto.UserResponse(
                    user.getId(), user.getUsername(), user.getRole());

            AuthResponse authResponse = new AuthResponse(jwt, expiresIn, userResponse);
            return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", authResponse));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuário ou senha inválidos"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Nome de usuário já existe",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<com.example.musicapi.dto.UserResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Nome de usuário já está em uso"));
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

        user = userRepository.save(user);

        com.example.musicapi.dto.UserResponse userResponse = new com.example.musicapi.dto.UserResponse(
                user.getId(), user.getUsername(), user.getRole());

        return ResponseEntity.ok(ApiResponse.success("Usuário registrado com sucesso", userResponse));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Atualizar token JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido ou expirado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<AuthResponse>> refreshToken(Authentication authentication) {
        try {
            String jwt = tokenProvider.generateToken(authentication);
            long expiresIn = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();

            // Buscar dados do usuário
            String username = authentication.getName();
            com.example.musicapi.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            com.example.musicapi.dto.UserResponse userResponse = new com.example.musicapi.dto.UserResponse(
                    user.getId(), user.getUsername(), user.getRole());

            AuthResponse authResponse = new AuthResponse(jwt, expiresIn, userResponse);
            return ResponseEntity.ok(ApiResponse.success("Token atualizado com sucesso", authResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Erro ao atualizar token: " + e.getMessage()));
        }
    }
}
