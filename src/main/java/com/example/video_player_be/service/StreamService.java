package com.example.video_player_be.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Slf4j
@Service
public class StreamService {
    @Value("${app.streams-folder}")
    private String streamsFolder;
    @Value("${app.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;

    public String startStream(String rtspUrl) throws IOException {
        String streamId = UUID.randomUUID().toString();
        File outputDir = new File(streamsFolder, streamId);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String[] command = getStrings(rtspUrl, outputDir);
        log.info("Starting FFmpeg process for stream: {}", streamId);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new Thread(() -> {
            reader.lines().forEach(line -> {
                log.info(line);
                if (line.contains("Invalid data") || line.contains("Connection refused")) {
                    log.warn("Stream seems to be down, stopping FFmpeg...");
                    process.destroy();
                }
            });
        }).start();

        return streamId;
    }

    private String[] getStrings(String rtspUrl, File outputDir) {
        File outputFile = new File(outputDir, "index.m3u8");
        String outputPath = outputFile.getAbsolutePath();
        return new String[]{
                ffmpegPath,
                "-rtsp_transport",
                "tcp", "-i", rtspUrl,
                "-fflags", "+genpts",
                "-use_wallclock_as_timestamps", "1",
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-c:a", "aac",
                "-b:a", "128k",
                "-ar", "44100",
                "-ac", "2",
                "-af", "aresample=async=1",
                "-f", "hls",
                "-hls_time", "2",
                "-hls_list_size", "0",
                "-hls_flags", "delete_segments+append_list",
                "-hls_start_number_source", "0",
                outputPath
        };
    }
}
