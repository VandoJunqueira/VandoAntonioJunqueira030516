package com.example.musicapi.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class AlbumRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private Integer releaseYear;

    private List<Long> artistIds;

    public AlbumRequest() {
    }

    public AlbumRequest(String title, Integer releaseYear) {
        this.title = title;
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return this.releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<Long> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
    }
}
