package com.example.musicapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.dto.ApiResponse;
import com.example.musicapi.dto.UserResponse;
import com.example.musicapi.dto.UserUpdateRequest;
import com.example.musicapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Endpoints para Gerenciamento de Usuários")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários registrados")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Usuários listados com sucesso", users));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar detalhes do usuário")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            UserResponse updated = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("Usuário atualizado com sucesso", updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuário não encontrado com ID: " + id));
        }
    }
}
