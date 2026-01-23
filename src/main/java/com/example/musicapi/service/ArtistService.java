package com.example.musicapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.ArtistRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Page<Artist> findAll(String name, Pageable pageable) {
        Specification<Artist> spec = (root, query, cb) -> {
            if (StringUtils.hasText(name)) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return null;
        };
        Page<Artist> page = artistRepository.findAll(spec, pageable);
        return page;
    }

    public Artist create(Artist artist) {
        return artistRepository.save(artist);
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
    }
}
