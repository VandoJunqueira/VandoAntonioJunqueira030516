package com.example.musicapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.musicapi.model.Regional;

public interface RegionalRepository extends JpaRepository<Regional, Long> {

    List<Regional> findByAtivoTrue();
}
