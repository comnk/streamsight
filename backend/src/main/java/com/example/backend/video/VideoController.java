package com.example.backend.video;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {
    @PostMapping("/videos")
    public Video postVideo() {
        Video video = new Video();
        return video;
    }

    @GetMapping("/videos/{id}")
    public Video getVideo(@PathVariable Long id) {
        Video video = new Video();
        return video;
    }
}
