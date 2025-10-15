package com.example.video_player_be.controller;

import com.example.video_player_be.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String streamUrl = "http://localhost:" + port + "/streams/" + streamId + "/index.m3u8";

            System.out.println("===============" + streamUrl);

            return ResponseEntity.ok(streamUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
