package com.example.backend.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoStorageService storageService;

    @Mock
    private VideoMetadataService metadataService;

    @InjectMocks
    private VideoController controller;

    @Test
    void uploadVideo_withEmptyFile_returnsBadRequest() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "clip.mp4", "video/mp4", new byte[0]);

        ResponseEntity<Video> response = controller.uploadVideo(emptyFile);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(storageService, metadataService, videoRepository);
    }

    @Test
    void uploadVideo_withValidFile_storesAndReturnsCreatedVideo() {
        MockMultipartFile file = new MockMultipartFile("file", "clip.mp4", "video/mp4", "content".getBytes());
        when(storageService.store(file)).thenReturn("/uploads/generated-name.mp4");
        when(metadataService.extractDurationSeconds("/uploads/generated-name.mp4")).thenReturn(42L);
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ResponseEntity<Video> response = controller.uploadVideo(file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Video body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getFilename()).isEqualTo("clip.mp4");
        assertThat(body.getFileSize()).isEqualTo(file.getSize());
        assertThat(body.getDuration()).isEqualTo(42L);
        assertThat(body.getStoragePath()).isEqualTo("/uploads/generated-name.mp4");
        assertThat(body.getStatus()).isEqualTo(VideoStatus.UPLOADED);
    }

    @Test
    void getVideo_whenFound_returnsOk() {
        Video video = new Video();
        video.setId(5L);
        when(videoRepository.findById(5L)).thenReturn(Optional.of(video));

        ResponseEntity<Video> response = controller.getVideo(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(video);
    }

    @Test
    void getVideo_whenMissing_returnsNotFound() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Video> response = controller.getVideo(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
