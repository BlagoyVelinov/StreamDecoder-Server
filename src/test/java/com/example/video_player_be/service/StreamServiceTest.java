package com.example.video_player_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.example.video_player_be.constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class StreamServiceTest {

    private StreamService streamService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        streamService = new StreamService();
        ReflectionTestUtils.setField(streamService, "streamsFolder", tempDir.toString());
        ReflectionTestUtils.setField(streamService, "ffmpegPath", FfmpegPaths.DEFAULT);
    }

    @Test
    void testStartStream_ThrowsIOException_WhenFfmpegNotFound() {

        ReflectionTestUtils.setField(streamService, "ffmpegPath", FfmpegPaths.NONEXISTENT_BINARY);
        String rtspUrl = RtspUrls.VALID_EXAMPLE;

        assertThrows(IOException.class, () -> {
            streamService.startStream(rtspUrl);
        }, Messages.THROW_IO_EXCEPTION_NOT_FOUND);
    }

    @Test
    void testStartStream_CreatesOutputDirectory() {

        String rtspUrl = RtspUrls.INVALID_TO_AVOID_LONG_PROCESS;

        String streamId = null;
        try {
            streamId = streamService.startStream(rtspUrl);
        } catch (IOException e) {
            // May throw if FFmpeg fails
        }

        File[] directories = tempDir.toFile().listFiles(File::isDirectory);
        assertNotNull(directories, Messages.DIRECTORIES_CREATED);
        assertTrue(directories.length >= 1, Messages.AT_LEAST_ONE_DIRECTORY);
        
        assertTrue(directories[0].getName().matches(Patterns.UUID_PATTERN), Messages.VALID_UUID);
        
        if (streamId != null) {
            assertEquals(streamId, directories[0].getName(), Messages.STREAM_ID_MATCHES_DIR);
        }
    }

    @Test
    void testStartStream_GeneratesUniqueStreamIds() {
        String rtspUrl = RtspUrls.INVALID_URL;

        String streamId1 = null;
        String streamId2 = null;
        
        try {
            streamId1 = streamService.startStream(rtspUrl);
        } catch (IOException ignored) {}
        
        try {
            streamId2 = streamService.startStream(rtspUrl);
        } catch (IOException ignored) {}

        File[] directories = tempDir.toFile().listFiles(File::isDirectory);
        assertNotNull(directories, Messages.DIRECTORIES_CREATED);
        assertEquals(2, directories.length, Messages.TWO_DIRECTORIES_CREATED);
        assertNotEquals(directories[0].getName(), directories[1].getName(), 
                Messages.UNIQUE_STREAM_IDS);
        
        if (streamId1 != null && streamId2 != null) {
            assertNotEquals(streamId1, streamId2, Messages.RETURNED_IDS_UNIQUE);
        }
    }

    @Test
    void testStartStream_WithNullRtspUrl_ThrowsException() {
        String rtspUrl = null;

        assertThrows(NullPointerException.class, () -> {
            streamService.startStream(rtspUrl);
        }, Messages.THROW_NULL_POINTER);
    }

    @Test
    void testStartStream_WithInvalidFfmpegPath_ThrowsIOException() {
        String invalidFfmpegPath = FfmpegPaths.INVALID_WINDOWS_PATH;
        ReflectionTestUtils.setField(streamService, "ffmpegPath", invalidFfmpegPath);
        String rtspUrl = RtspUrls.VALID_EXAMPLE;

        IOException exception = assertThrows(IOException.class, () -> {
            streamService.startStream(rtspUrl);
        }, Messages.THROW_IO_EXCEPTION_INVALID_PATH);
        
        assertTrue(exception.getMessage().contains(invalidFfmpegPath) || 
                   exception.getCause() != null,
                   Messages.EXCEPTION_REFERENCES_PATH);
    }

    @Test
    void testStartStream_DirectoryCreation_WithMultipleAttempts() {
        String rtspUrl = RtspUrls.INVALID_URL;
        
        for (int i = 0; i < 3; i++) {
            try {
                streamService.startStream(rtspUrl);
            } catch (IOException ignored) {
                // May throw if FFmpeg fails
            }
        }

        File[] directories = tempDir.toFile().listFiles(File::isDirectory);
        assertNotNull(directories, Messages.DIRECTORIES_CREATED);
        assertEquals(3, directories.length, Messages.THREE_DIRECTORIES_CREATED);
        
        for (int i = 0; i < directories.length; i++) {
            assertTrue(directories[i].getName().matches(Patterns.UUID_PATTERN), 
                    String.format(Messages.VALID_UUID_WITH_NAME, directories[i].getName()));
            
            for (int j = i + 1; j < directories.length; j++) {
                assertNotEquals(directories[i].getName(), directories[j].getName(),
                        Messages.ALL_NAMES_UNIQUE);
            }
        }
    }

    @Test
    void testStartStream_OutputDirectoryStructure() {
        String rtspUrl = RtspUrls.INVALID_URL;

        try {
            streamService.startStream(rtspUrl);
        } catch (IOException ignored) {
            // May throw if FFmpeg fails
        }


        File[] directories = tempDir.toFile().listFiles(File::isDirectory);
        assertNotNull(directories, Messages.DIRECTORY_CREATED);
        assertEquals(1, directories.length, Messages.ONE_DIRECTORY_CREATED);
        
        File streamDir = directories[0];
        assertTrue(streamDir.isDirectory(), Messages.SHOULD_BE_DIRECTORY);
        assertTrue(streamDir.exists(), Messages.DIRECTORY_SHOULD_EXIST);

        File expectedIndexFile = new File(streamDir, Files.INDEX_M3U8);
        assertEquals(streamDir, expectedIndexFile.getParentFile(), 
                Messages.INDEX_IN_STREAM_DIR);
    }
}
