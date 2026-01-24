package com.example.musicapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicapi.dto.ApiResponse;
import com.example.musicapi.dto.ArtistRequest;
import com.example.musicapi.model.Artist;
import com.example.musicapi.service.ArtistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artists", description = "Management of Artists")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    @Operation(summary = "List all artists with pagination and filters")
    public ResponseEntity<Page<Artist>> getAllArtists(@RequestParam(required = false) String name,
            Pageable pageable) {
        return ResponseEntity.ok(artistService.findAll(name, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get artist by ID")
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Artist>> getArtistById(@PathVariable Long id) {
        try {
            Artist artist = artistService.findById(id);
            return ResponseEntity.ok(ApiResponse.success("Artista encontrado com sucesso", artist));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Artista não encontrado com ID: " + id));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new artist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Artist>> createArtist(@Valid @RequestBody ArtistRequest request) {
        Artist created = artistService.create(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success("Artista criado com sucesso", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an artist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Artist>> updateArtist(@PathVariable Long id, @Valid @RequestBody ArtistRequest request) {
        try {
            Artist updated = artistService.update(id, request);
            return ResponseEntity.ok(ApiResponse.success("Artista atualizado com sucesso", updated));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Artista não encontrado com ID: " + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an artist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Void>> deleteArtist(@PathVariable Long id) {
        try {
            artistService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Artista deletado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Artista não encontrado com ID: " + id));
        }
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload artist image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Artist>> uploadImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Artist artist = artistService.uploadImage(id, file);
            return ResponseEntity.ok(ApiResponse.success("Imagem do artista enviada com sucesso", artist));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Erro ao enviar imagem: " + e.getMessage()));
        }
    }
}
