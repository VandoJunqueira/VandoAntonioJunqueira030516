package com.example.musicapi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.musicapi.model.Album;
import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public Page<Album> findAll(Pageable pageable) {
        Page<Album> page = albumRepository.findAll(pageable);
        return page;
    }

    public Album create(Album album, List<Long> artistIds) {
        if (artistIds != null && !artistIds.isEmpty()) {
            Set<Artist> artists = new HashSet<>(artistRepository.findAllById(artistIds));
            album.setArtists(artists);
        }
        return albumRepository.save(album);
    }

    public Album findById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com ID: " + id));
    }

    public Album update(Long id, Album albumRequest, List<Long> artistIds) {
        Album album = findById(id);
        album.setTitle(albumRequest.getTitle());
        if (albumRequest.getReleaseYear() != null) {
            album.setReleaseYear(albumRequest.getReleaseYear());
        }

        if (artistIds != null) {
            Set<Artist> artists = new HashSet<>(artistRepository.findAllById(artistIds));
            album.setArtists(artists);
        }

        return albumRepository.save(album);
    }

    public void delete(Long id) {
        Album album = findById(id);
        albumRepository.delete(album);
    }
}
