# StreamDecoder-Server

A Spring Boot-based video streaming service that converts RTSP streams into HLS (HTTP Live Streaming) format for web playback. The server uses FFmpeg to process RTSP video streams and generates HLS playlists and segments that can be consumed by web browsers and video players.

## What This Server Does

This server provides a REST API for managing video streams:
- **Converts RTSP streams to HLS format** - Takes RTSP video sources (like IP cameras) and transcodes them into HLS format
- **Manages multiple concurrent streams** - Supports starting, tracking, and stopping multiple video streams simultaneously
- **Automatic cleanup** - Handles FFmpeg process lifecycle and cleans up resources when streams are deleted
- **Web-compatible output** - Generates HLS playlists (.m3u8) and video segments that can be played in web browsers

## Prerequisites

- Java 17 or higher
- FFmpeg installed and accessible in system PATH
- Gradle (included via wrapper)

## API Endpoints

### 1. Start a Stream
**POST** `/api/stream/start`

Starts a new stream from an RTSP source and returns the HLS playlist URL.

**Query Parameters:**
- `rtspUrl` (required) - The RTSP URL of the video source

**Example Request:**
```bash
POST http://localhost:8082/api/stream/start?rtspUrl=rtsp://example.com/stream
```

**Example Response:**
```
http://localhost:8082/streams/a1b2c3d4-e5f6-7890-abcd-ef1234567890/index.m3u8
```

**Description:** Creates a unique stream ID, starts an FFmpeg process to transcode the RTSP stream to HLS format, and returns the URL where the HLS playlist can be accessed.

---

### 2. Delete a Stream
**DELETE** `/api/stream/delete/{id}`

Stops a running stream and deletes all associated files.

**Path Parameters:**
- `id` (required) - The stream ID to delete

**Example Request:**
```bash
DELETE http://localhost:8082/api/stream/delete/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Example Response:**
```
Stream deleted successfully: a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Description:** Terminates the FFmpeg process associated with the stream, removes it from active streams tracking, and deletes the stream directory containing all HLS segments and playlists.

---

### 3. Get All Streams
**GET** `/api/stream/all`

Retrieves a list of all stream IDs currently available on the server.

**Example Request:**
```bash
GET http://localhost:8082/api/stream/all
```

**Example Response:**
```json
[
  "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "b2c3d4e5-f6a7-8901-bcde-f12345678901"
]
```

**Description:** Scans the streams directory and returns all stream IDs. This includes both active streams (currently being processed) and any streams that may have been stopped but not yet deleted.

---

## Running the Server

### Local Development

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd video_player_be
   ```

2. **Configure application properties:**
   Edit `src/main/resources/application.properties` or `application.yml` to set:
   - `app.streams-folder` - Directory where stream files will be stored
   - `app.ffmpeg-path` - Path to FFmpeg executable (defaults to "ffmpeg" if in PATH)
   - `server.port` - Server port (default: 8082)

3. **Build and run:**
   ```bash
   ./gradlew bootRun
   ```

The server will start on `http://localhost:8082`

---

## Docker Deployment

### Using Pre-built Docker Image

The easiest way to run the server is using the pre-built Docker image:

**Pull the Docker image:**
```bash
docker pull blagoyvelinov/stream_server
```

**Start the container:**
```bash
docker run -d -p 8082:8082 --name stream_service_container blagoyvelinov/stream_server
```

**Options:**
- `-d` - Run in detached mode (background)
- `-p 8082:8082` - Map port 8082 from container to host
- `--name stream_service_container` - Name the container for easy reference

**Stop the container:**
```bash
docker stop stream_service_container
```

**Remove the container:**
```bash
docker rm stream_service_container
```

**View logs:**
```bash
docker logs stream_service_container
```

---

## Usage Example

1. **Start a stream:**
   ```bash
   curl -X POST "http://localhost:8082/api/stream/start?rtspUrl=rtsp://your-camera-url/stream"
   ```
   
   Response: `http://localhost:8082/streams/abc123.../index.m3u8`

2. **Play the stream in a video player or browser** using the returned URL

3. **List all streams:**
   ```bash
   curl http://localhost:8082/api/stream/all
   ```

4. **Delete a stream when done:**
   ```bash
   curl -X DELETE http://localhost:8082/api/stream/delete/abc123...
   ```

---

## Technical Details

- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **Build Tool:** Gradle
- **Video Processing:** FFmpeg
- **Output Format:** HLS (HTTP Live Streaming)
- **Concurrency:** Thread-safe stream management using ConcurrentHashMap

---

## Configuration

Key configuration properties in `application.properties`:

```properties
server.port=8082
app.streams-folder=./streams
app.ffmpeg-path=ffmpeg
```

---

## Notes

- Ensure FFmpeg is installed and accessible on the system
- The server requires sufficient disk space for storing HLS segments
- Stream files are stored in the configured `streams-folder` directory
- Each stream gets a unique UUID as its identifier
- HLS segments are automatically managed by FFmpeg with the `delete_segments` flag
