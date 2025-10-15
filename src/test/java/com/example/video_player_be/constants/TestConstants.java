package com.example.video_player_be.constants;

/**
 * Constants used across test classes.
 * Centralizes test data for consistency and maintainability.
 */
public final class TestConstants {

    private TestConstants() {
        // Prevent instantiation
    }

    /**
     * RTSP URL constants for testing
     */
    public static final class RtspUrls {
        public static final String VALID_EXAMPLE = "rtsp://example.com/stream";
        public static final String INVALID_URL = "rtsp://invalid-url";
        public static final String INVALID_TO_AVOID_LONG_PROCESS = "rtsp://invalid-url-to-avoid-long-running-process";
        public static final String WITH_SPECIAL_CHARS = "rtsp://user:pass@example.com:554/stream?param=value";
        public static final String STREAM_1 = "rtsp://example.com/stream1";
        public static final String STREAM_2 = "rtsp://example.com/stream2";
        public static final String STREAM_3 = "rtsp://example.com/stream3";
        public static final String EMPTY = "";

        private RtspUrls() {}
    }

    /**
     * FFmpeg path constants for testing
     */
    public static final class FfmpegPaths {
        public static final String DEFAULT = "ffmpeg";
        public static final String NONEXISTENT_BINARY = "nonexistent_ffmpeg_binary";
        public static final String INVALID_WINDOWS_PATH = "C:\\nonexistent\\path\\ffmpeg.exe";
        public static final String CUSTOM_UNIX_PATH = "/usr/local/bin/ffmpeg";

        private FfmpegPaths() {}
    }

    /**
     * File and directory constants
     */
    public static final class Files {
        public static final String INDEX_M3U8 = "index.m3u8";

        private Files() {}
    }

    /**
     * Regex patterns for validation
     */
    public static final class Patterns {
        public static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        private Patterns() {}
    }

    /**
     * Test assertion messages
     */
    public static final class Messages {
        public static final String STREAM_ID_NOT_NULL = "Stream ID should not be null";
        public static final String STREAM_ID_NOT_EMPTY = "Stream ID should not be empty";
        public static final String DIRECTORIES_CREATED = "Directories should be created";
        public static final String DIRECTORY_CREATED = "Directory should be created";
        public static final String AT_LEAST_ONE_DIRECTORY = "At least one directory should be created";
        public static final String ONE_DIRECTORY_CREATED = "One directory should be created";
        public static final String TWO_DIRECTORIES_CREATED = "Two directories should be created";
        public static final String THREE_DIRECTORIES_CREATED = "Three directories should be created";
        public static final String SHOULD_BE_DIRECTORY = "Should be a directory";
        public static final String DIRECTORY_SHOULD_EXIST = "Directory should exist";
        public static final String UNIQUE_STREAM_IDS = "Each stream should have a unique ID";
        public static final String RETURNED_IDS_UNIQUE = "Returned stream IDs should be unique";
        public static final String ALL_NAMES_UNIQUE = "All directory names should be unique";
        public static final String VALID_UUID = "Directory name should be a valid UUID";
        public static final String VALID_UUID_WITH_NAME = "Directory name should be a valid UUID: %s";
        public static final String STREAM_ID_MATCHES_DIR = "Stream ID should match directory name";
        public static final String INDEX_IN_STREAM_DIR = "Index file should be in the stream directory";
        public static final String THROW_IO_EXCEPTION_NOT_FOUND = "Should throw IOException when FFmpeg is not found";
        public static final String THROW_IO_EXCEPTION_INVALID_PATH = "Should throw IOException when FFmpeg path doesn't exist";
        public static final String THROW_NULL_POINTER = "Should throw NullPointerException for null RTSP URL";
        public static final String EXCEPTION_REFERENCES_PATH = "Exception should reference the FFmpeg path or have a cause";

        private Messages() {}
    }
}
