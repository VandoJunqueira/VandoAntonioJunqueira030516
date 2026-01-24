package com.example.musicapi.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String bucketName);

    String getPresignedUrl(String objectName, String bucketName);
}
