package com.example.video_player_be.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class StreamService {
    @Value("${app.streams-folder}")
    private String streamsFolder;
    @Value("${app.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;
    
    private final Map<String, Process> activeStreams = new ConcurrentHashMap<>();

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
        activeStreams.put(streamId, process);
        
        new Thread(() -> {
            reader.lines().forEach(line -> {
                log.info(line);
                if (line.contains("Invalid data") || line.contains("Connection refused")) {
                    log.warn("Stream seems to be down, stopping FFmpeg...");
                    process.destroy();
                    activeStreams.remove(streamId);
                }
            });
        }).start();

        return streamId;
    }

    public void deleteStream(String streamId) throws IOException {
        log.info("Deleting stream: {}", streamId);

        Process process = activeStreams.get(streamId);
        if (process != null && process.isAlive()) {
            log.info("Stopping FFmpeg process for stream: {}", streamId);
            process.destroy();
            activeStreams.remove(streamId);
        }

        File streamDir = new File(streamsFolder, streamId);
        if (streamDir.exists()) {
            log.info("Deleting stream directory: {}", streamDir.getAbsolutePath());
            deleteDirectory(streamDir);
        } else {
            log.warn("Stream directory not found: {}", streamId);
            throw new IOException("Stream not found: " + streamId);
        }
    }

    public List<String> getAllStreams() {
        log.info("Getting all streams from folder: {}", streamsFolder);
        File streamsDir = new File(streamsFolder);

        if (!streamsDir.exists() || !streamsDir.isDirectory()) {
            log.warn("Streams folder does not exist: {}", streamsFolder);
            return new ArrayList<>();
        }

        File[] streamDirs = streamsDir.listFiles(File::isDirectory);
        if (streamDirs == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(streamDirs)
                .map(File::getName)
                .collect(Collectors.toList());
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
                "-rtbufsize", "64M",
                "-preset", "ultrafast",
                "-c:a", "aac",
                "-b:a", "128k",
                "-ar", "44100",
                "-ac", "2",
                "-af", "aresample=async=1",
                "-f", "hls",
                "-hls_time", "5",
                "-hls_list_size", "0",
                "-hls_flags", "delete_segments+append_list",
                "-hls_start_number_source", "0",
                outputPath
        };
    }
    
    private void deleteDirectory(File directory) throws IOException {
        try (Stream<Path> walk = Files.walk(directory.toPath())) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
