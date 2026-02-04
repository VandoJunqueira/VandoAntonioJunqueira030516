package com.example.musicapi.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.musicapi.model.Album;
import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AlbumService albumService;

    /**
     * Verifica se a listagem de álbuns retorna uma página contendo os álbuns
     * esperados.
     */
    @Test
    void findAll_ShouldReturnPagedAlbums() {
        Album album = new Album(1L, "Test Album", "cover.jpg", 2022, LocalDateTime.now(), LocalDateTime.now());
        Page<Album> page = new PageImpl<>(List.of(album));

        when(albumRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Album> result = albumService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Album", result.getContent().get(0).getTitle());
    }

    /**
     * Testa a criação de um novo álbum, garantindo que ele seja salvo no
     * repositório.
     */
    @Test
    void create_ShouldSaveAlbum() {
        Album album = new Album();
        album.setTitle("New Album");

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        Album result = albumService.create(album, Collections.emptyList());

        assertNotNull(result);
        assertEquals("New Album", result.getTitle());
        verify(albumRepository).save(any(Album.class));
    }

    /**
     * Valida a adição de um artista a um álbum, assegurando que a relação seja
     * salva.
     */
    @Test
    void addArtistToAlbum_ShouldAddArtist() {
        Album album = new Album();
        album.setId(1L);

        Artist artist = new Artist();
        artist.setId(10L);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(10L)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        Album result = albumService.addArtistToAlbum(1L, 10L);

        assertTrue(result.getArtists().contains(artist));
        verify(albumRepository).save(album);
    }
}
