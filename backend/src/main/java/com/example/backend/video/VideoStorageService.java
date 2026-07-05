package com.example.backend.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoStorageService {

    private final Path storageRoot;

    public VideoStorageService(@Value("${app.storage.location}") String storageLocation) {
        this.storageRoot = Path.of(storageLocation);
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create storage directory: " + storageRoot, e);
        }
    }

    public String store(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String storedName = UUID.randomUUID() + extension;
        Path destination = storageRoot.resolve(storedName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store uploaded file", e);
        }
        return destination.toString();
    }
}
