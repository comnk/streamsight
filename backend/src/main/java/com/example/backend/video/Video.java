package com.example.backend.video;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.Data;

@Entity
@Table(name = "videos")
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String title;

    private Instant timestamp;

    private Long fileSize;

    private Long duration;

    private String storagePath;

    @Enumerated(EnumType.STRING)
    private VideoStatus status;

    public Video() {
    }
}
