package com.example.video_player_be.controller;

import com.example.video_player_be.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStream(@PathVariable String id) {
        try {
            streamService.deleteStream(id);
            return ResponseEntity.ok("Stream deleted successfully: " + id);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllStreams() {
        List<String> streams = streamService.getAllStreams();
        return ResponseEntity.ok(streams);
    }
}
