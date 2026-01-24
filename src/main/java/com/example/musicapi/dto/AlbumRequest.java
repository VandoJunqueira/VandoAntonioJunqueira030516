package com.example.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class AlbumRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private Integer releaseYear;

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
}
