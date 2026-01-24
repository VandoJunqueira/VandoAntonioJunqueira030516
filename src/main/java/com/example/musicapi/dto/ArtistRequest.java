package com.example.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class ArtistRequest {

    @NotBlank(message = "Artist name is required")
    private String name;

    public ArtistRequest() {
    }

    public ArtistRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
