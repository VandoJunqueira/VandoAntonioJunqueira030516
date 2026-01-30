package com.example.musicapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.dto.ApiResponse;
import com.example.musicapi.model.Regional;
import com.example.musicapi.service.RegionalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/regionais")
@Tag(name = "Regionais", description = "Endpoints para gerenciar Regionais")
public class RegionalController {

    private final RegionalService regionalService;

    public RegionalController(RegionalService regionalService) {
        this.regionalService = regionalService;
    }

    @GetMapping
    @Operation(summary = "Obter todas as Regionais", description = "Retorna uma lista de todas as regionais ativas, sincronizando com a API externa se necessário.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de regionais retornada com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<Regional>>> getAllRegionais() {
        List<Regional> regionais = regionalService.findAllSynced();
        return ResponseEntity.ok(ApiResponse.success("Regionais listadas com sucesso", regionais));
    }
}
