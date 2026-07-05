package com.example.backend.video;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoRepository videoRepository;
    private final VideoStorageService storageService;
    private final VideoMetadataService metadataService;

    public VideoController(VideoRepository videoRepository, VideoStorageService storageService,
            VideoMetadataService metadataService) {
        this.videoRepository = videoRepository;
        this.storageService = storageService;
        this.metadataService = metadataService;
    }

    @PostMapping
    public ResponseEntity<Video> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String storagePath = storageService.store(file);
        Long duration = metadataService.extractDurationSeconds(storagePath);

        Video video = new Video();
        video.setFilename(file.getOriginalFilename());
        video.setFileSize(file.getSize());
        video.setTimestamp(Instant.now());
        video.setStoragePath(storagePath);
        video.setDuration(duration);
        video.setStatus(VideoStatus.UPLOADED);

        Video saved = videoRepository.save(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Video> listVideos() {
        return videoRepository.findAll();
    }
}
