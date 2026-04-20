package edu.eci.tdse.securetwitter.stream.controller;

import edu.eci.tdse.securetwitter.stream.dto.PostResponse;
import edu.eci.tdse.securetwitter.stream.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Stream", description = "Public post feed — no authentication required")
public class StreamController {

    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/stream")
    @Operation(
            summary = "Get the global public stream",
            description = "Returns all posts in reverse chronological order. Public endpoint."
    )
    @ApiResponse(responseCode = "200", description = "List of posts",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    public ResponseEntity<List<PostResponse>> getStream() {
        return ResponseEntity.ok(streamService.getStream().stream().map(PostResponse::from).toList());
    }

    @GetMapping("/posts")
    @Operation(
            summary = "Get all posts (alias of /stream)",
            description = "Returns all posts in reverse chronological order. Public endpoint."
    )
    @ApiResponse(responseCode = "200", description = "List of posts",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(streamService.getStream().stream().map(PostResponse::from).toList());
    }
}
