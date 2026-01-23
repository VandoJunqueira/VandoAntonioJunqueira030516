package com.example.musicapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.model.Artist;
import com.example.musicapi.service.ArtistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new artist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artist> createArtist(@RequestBody Artist artist) {
        Artist created = artistService.create(artist);
        return ResponseEntity.ok(created);
    }
}
