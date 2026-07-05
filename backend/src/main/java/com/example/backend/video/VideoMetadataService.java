package com.example.backend.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VideoMetadataService {

    private static final Logger log = LoggerFactory.getLogger(VideoMetadataService.class);

    public Long extractDurationSeconds(String storagePath) {
        ProcessBuilder builder = new ProcessBuilder(
                "ffprobe", "-v", "error",
                "-show_entries", "format=duration",
                "-of", "csv=p=0",
                storagePath);
        builder.redirectErrorStream(false);
        try {
            Process process = builder.start();
            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = reader.readLine();
            }
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished || process.exitValue() != 0 || output == null) {
                log.warn("ffprobe failed to determine duration for {}", storagePath);
                return null;
            }
            return Math.round(Double.parseDouble(output.trim()));
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.warn("Could not extract duration for {}: {}", storagePath, e.getMessage());
            return null;
        }
    }
}
