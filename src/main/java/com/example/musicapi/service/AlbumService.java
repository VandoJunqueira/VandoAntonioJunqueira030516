package com.example.musicapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.musicapi.model.Album;
import com.example.musicapi.repository.AlbumRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Page<Album> findAll(Pageable pageable) {
        Page<Album> page = albumRepository.findAll(pageable);
        return page;
    }

    public Album create(Album album) {
        return albumRepository.save(album);
    }

    public Album findById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com ID: " + id));
    }

    public Album update(Long id, Album albumRequest) {
        Album album = findById(id);
        album.setTitle(albumRequest.getTitle());
        if (albumRequest.getReleaseYear() != null) {
            album.setReleaseYear(albumRequest.getReleaseYear());
        }
        return albumRepository.save(album);
    }

    public void delete(Long id) {
        Album album = findById(id);
        albumRepository.delete(album);
    }
}
