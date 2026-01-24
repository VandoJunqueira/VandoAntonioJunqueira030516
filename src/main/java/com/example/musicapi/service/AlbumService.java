package com.example.musicapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.musicapi.model.Album;
import com.example.musicapi.repository.AlbumRepository;

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
}
