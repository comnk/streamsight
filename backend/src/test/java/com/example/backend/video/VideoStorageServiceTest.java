package com.example.backend.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class VideoStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void store_preservesFileExtensionAndContent() throws IOException {
        VideoStorageService service = new VideoStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile("file", "clip.mp4", "video/mp4", "hello".getBytes());

        String storedPath = service.store(file);

        Path stored = Path.of(storedPath);
        assertThat(stored).exists();
        assertThat(stored.getParent()).isEqualTo(tempDir);
        assertThat(stored.getFileName().toString()).endsWith(".mp4");
        assertThat(stored.getFileName().toString()).isNotEqualTo("clip.mp4");
        assertThat(Files.readString(stored)).isEqualTo("hello");
    }

    @Test
    void store_withNoExtension_storesFileWithoutExtension() throws IOException {
        VideoStorageService service = new VideoStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile("file", "clip-no-extension", "video/mp4", "hello".getBytes());

        String storedPath = service.store(file);

        Path stored = Path.of(storedPath);
        assertThat(stored).exists();
        assertThat(stored.getFileName().toString()).doesNotContain(".");
    }

    @Test
    void constructor_createsStorageDirectoryIfMissing() {
        Path nested = tempDir.resolve("nested/uploads");

        new VideoStorageService(nested.toString());

        assertThat(nested).exists().isDirectory();
    }
}
