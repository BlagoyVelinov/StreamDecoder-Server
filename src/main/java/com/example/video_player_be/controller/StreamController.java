package com.example.video_player_be.controller;

import com.example.video_player_be.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    @Value("${server.port}")
    private int port;

    @PostMapping("/start")
    public ResponseEntity<?> startStream(@RequestParam String rtspUrl) {
        try {
            String streamId = streamService.startStream(rtspUrl);
            String streamUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/streams/")
                    .path(streamId + "/index.m3u8")
                    .toUriString();

            return ResponseEntity.ok(streamUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
