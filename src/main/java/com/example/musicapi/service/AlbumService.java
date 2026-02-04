package com.example.musicapi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicapi.model.Album;
import com.example.musicapi.model.Artist;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.minio.bucket-albums}")
    private String albumBucket;

    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository,
            FileStorageService fileStorageService, org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.fileStorageService = fileStorageService;
        this.messagingTemplate = messagingTemplate;
    }

    public Page<Album> findAll(Pageable pageable) {
        Page<Album> page = albumRepository.findAll(pageable);
        page.forEach(this::enrichAlbumWithUrl);
        return page;
    }

    public Album create(Album album, List<Long> artistIds) {
        if (artistIds != null && !artistIds.isEmpty()) {
            Set<Artist> artists = new HashSet<>(artistRepository.findAllById(artistIds));
            album.setArtists(artists);
        }

        enrichAlbumWithUrl(album);
        Album savedAlbum = albumRepository.save(album);
        messagingTemplate.convertAndSend("/topic/albums", savedAlbum);
        return savedAlbum;
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

        enrichAlbumWithUrl(album);
        return albumRepository.save(album);
    }

    public void delete(Long id) {
        Album album = findById(id);
        albumRepository.delete(album);
    }

    public Album addArtistToAlbum(Long albumId, Long artistId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com ID: " + albumId));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado com ID: " + artistId));

        if (!album.getArtists().contains(artist)) {
            album.getArtists().add(artist);
            album = albumRepository.save(album);
        }
        enrichAlbumWithUrl(album);
        return album;
    }

    public Album removeArtistFromAlbum(Long albumId, Long artistId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com ID: " + albumId));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado com ID: " + artistId));

        album.getArtists().remove(artist);
        album = albumRepository.save(album);
        enrichAlbumWithUrl(album);
        return album;
    }

    public Album uploadCover(Long id, MultipartFile file) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com ID: " + id));
        String fileName = fileStorageService.uploadFile(file, albumBucket);
        album.setCoverUrl(fileName);
        albumRepository.save(album);
        enrichAlbumWithUrl(album);
        return album;
    }

    private void enrichAlbumWithUrl(Album album) {
        if (album.getCoverUrl() != null && !album.getCoverUrl().startsWith("http")) {
            String url = fileStorageService.getPresignedUrl(album.getCoverUrl(), albumBucket);
            album.setCoverUrl(url);
        }
    }
}
