package com.example.musicapi.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.ArtistRepository;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ArtistService artistService;

    /**
     * Testa a busca paginada de artistas, verificando se o repositório é
     * chamado corretamente com especificações.
     */
    @Test
    void findAll_ShouldReturnPagedArtists() {
        Artist artist = new Artist();
        artist.setName("Test Artist");
        Page<Artist> page = new PageImpl<>(List.of(artist));

        when(artistRepository.findAll(ArgumentMatchers.<Specification<Artist>>any(), ArgumentMatchers.any(PageRequest.class))).thenReturn(page);

        Page<Artist> result = artistService.findAll("Test", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(artistRepository, times(1)).findAll(ArgumentMatchers.<Specification<Artist>>any(), ArgumentMatchers.any(PageRequest.class));
    }

    /**
     * Testa o método findById garantindo que ele retorne o artista correto
     * quando encontrado.
     */
    @Test
    void findById_ShouldReturnArtist() {
        Artist artist = new Artist();
        artist.setId(1L);
        artist.setName("Test Artist");

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        Artist result = artistService.findById(1L);

        assertEquals("Test Artist", result.getName());
    }
}
