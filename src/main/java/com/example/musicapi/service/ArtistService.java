package com.example.musicapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.ArtistRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.minio.bucket-artists}")
    private String artistBucket;

    public ArtistService(ArtistRepository artistRepository, FileStorageService fileStorageService) {
        this.artistRepository = artistRepository;
        this.fileStorageService = fileStorageService;
    }

    public Page<Artist> findAll(String name, Pageable pageable) {
        Specification<Artist> spec = (root, query, cb) -> {
            if (StringUtils.hasText(name)) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return null;
        };
        Page<Artist> page = artistRepository.findAll(spec, pageable);

        page.forEach(this::enrichArtistWithUrl);
        return page;
    }

    public Artist findById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
        enrichArtistWithUrl(artist);
        return artist;
    }

    public Artist create(Artist artist) {
        return artistRepository.save(artist);
    }

    public Artist update(Long id, Artist artistDetails) {
        Artist artist = findById(id);
        artist.setName(artistDetails.getName());
        artistRepository.save(artist);
        enrichArtistWithUrl(artist);
        return artist;
    }

    public void delete(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new EntityNotFoundException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
    }

    public Artist uploadImage(Long id, MultipartFile file) {
        Artist artist = findById(id);
        String fileName = fileStorageService.uploadFile(file, artistBucket);
        artist.setImageUrl(fileName);
        artistRepository.save(artist);
        enrichArtistWithUrl(artist);
        return artist;
    }

    private void enrichArtistWithUrl(Artist artist) {
        if (artist.getImageUrl() != null && !artist.getImageUrl().startsWith("http")) {
            String url = fileStorageService.getPresignedUrl(artist.getImageUrl(), artistBucket);
            artist.setImageUrl(url);
        }
    }
}
