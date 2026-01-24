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
import com.example.musicapi.dto.ApiResponse;
import com.example.musicapi.model.Album;
import com.example.musicapi.service.AlbumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Álbuns", description = "Gerenciamento de Álbuns")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os álbuns")
    public ResponseEntity<Page<Album>> getAllAlbums(Pageable pageable) {
        return ResponseEntity.ok(albumService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar álbum por ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Álbum encontrado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Álbum não encontrado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Album>> getAlbumById(@PathVariable Long id) {
        try {
            Album album = albumService.findById(id);
            return ResponseEntity.ok(ApiResponse.success("Álbum encontrado com sucesso", album));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Álbum não encontrado com ID: " + id));
        }
    }

    @PostMapping
    @Operation(summary = "Criar um novo álbum")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Álbum criado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Album>> createAlbum(@Valid @RequestBody AlbumRequest request) {
        Album album = new Album();
        album.setTitle(request.getTitle());
        album.setReleaseYear(request.getReleaseYear());

        Album created = albumService.create(album, request.getArtistIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Álbum criado com sucesso", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um álbum")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Álbum não encontrado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Album>> updateAlbum(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
        try {
            Album album = new Album();
            album.setTitle(request.getTitle());
            album.setReleaseYear(request.getReleaseYear());

            Album updated = albumService.update(id, album, request.getArtistIds());
            return ResponseEntity.ok(ApiResponse.success("Álbum atualizado com sucesso", updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Álbum não encontrado com ID: " + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um álbum")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Álbum deletado com sucesso",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Álbum não encontrado",
                content = @Content(schema = @Schema(implementation = com.example.musicapi.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.example.musicapi.dto.ApiResponse<Void>> deleteAlbum(@PathVariable Long id) {
        try {
            albumService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Álbum deletado com sucesso"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Álbum não encontrado com ID: " + id));
        }
    }
}
