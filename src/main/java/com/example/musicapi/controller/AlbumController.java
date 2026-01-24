package com.example.musicapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.musicapi.dto.AlbumRequest;
import com.example.musicapi.model.Album;
import com.example.musicapi.service.AlbumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Albums", description = "Management of Albums")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "List all albums")
    public ResponseEntity<Page<Album>> getAllAlbums(Pageable pageable) {
        return ResponseEntity.ok(albumService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get album by ID")
    public ResponseEntity<Album> getAlbumById(@PathVariable Long id) {
        try {
            Album album = albumService.findById(id);
            return ResponseEntity.ok(album);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new album")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody AlbumRequest request) {
        Album album = new Album();
        album.setTitle(request.getTitle());
        album.setReleaseYear(request.getReleaseYear());

        Album created = albumService.create(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an album")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
        try {
            Album album = new Album();
            album.setTitle(request.getTitle());
            album.setReleaseYear(request.getReleaseYear());

            Album updated = albumService.update(id, album);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an album")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        try {
            albumService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
