package edu.eci.tdse.securetwitter.controller;

import edu.eci.tdse.securetwitter.model.Post;
import edu.eci.tdse.securetwitter.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Posts", description = "Public stream and post creation")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all posts in reverse chronological order")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getStream());
    }

    @GetMapping("/stream")
    @Operation(summary = "Get the global public stream")
    public ResponseEntity<List<Post>> getStream() {
        return ResponseEntity.ok(postService.getStream());
    }

    @PostMapping("/posts")
    @Operation(
            summary = "Create a new post",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Post> createPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Post payload with max 140 characters",
                    content = @Content(
                            schema = @Schema(implementation = CreatePostRequest.class),
                            examples = @ExampleObject(value = "{\"content\":\"Hello secure-twitter!\"}")
                    )
            )
            @Valid @RequestBody CreatePostRequest request,
            JwtAuthenticationToken token
    ) {
        try {
            Post post = postService.createPost(
                    request.content(),
                    token.getName(),
                    (String) token.getTokenAttributes().getOrDefault("name", token.getName()),
                    (String) token.getTokenAttributes().get("picture")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    public record CreatePostRequest(
            @NotBlank(message = "content is required")
            @Size(max = 140, message = "content must be at most 140 characters")
            String content
    ) {
    }
}

