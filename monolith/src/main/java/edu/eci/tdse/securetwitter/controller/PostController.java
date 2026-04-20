package edu.eci.tdse.securetwitter.controller;

import edu.eci.tdse.securetwitter.dto.PostResponse;
import edu.eci.tdse.securetwitter.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(
        summary = "Get all posts (alias of /stream)",
        description = "Returns all posts in reverse chronological order. Public — no authentication required."
    )
    @ApiResponse(responseCode = "200", description = "List of posts",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.getStream().stream().map(PostResponse::from).toList());
    }

    @GetMapping("/stream")
    @Operation(
        summary = "Get the global public stream",
        description = "Returns all posts in reverse chronological order. Public — no authentication required."
    )
    @ApiResponse(responseCode = "200", description = "List of posts",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    public ResponseEntity<List<PostResponse>> getStream() {
        return ResponseEntity.ok(postService.getStream().stream().map(PostResponse::from).toList());
    }

    @PostMapping("/posts")
    @Operation(
        summary = "Create a new post",
        description = "Creates a post of up to 140 characters. Requires a valid Auth0 JWT Bearer token.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Post created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error: content missing or exceeds 140 characters",
        content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
        content = @Content(mediaType = "application/json"))
    public ResponseEntity<PostResponse> createPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Post payload — content max 140 characters",
                    content = @Content(
                            schema = @Schema(implementation = CreatePostRequest.class),
                            examples = @ExampleObject(value = "{\"content\":\"Hello secure-twitter!\"}")
                    )
            )
            @Valid @RequestBody CreatePostRequest request,
            JwtAuthenticationToken token
    ) {
        Jwt jwt = token.getToken();
        String authorAuth0Id = token.getName();
        String authorName = jwt.getClaimAsString("name");
        if (authorName == null || authorName.isBlank()) {
            authorName = authorAuth0Id;
        }

        PostResponse response = PostResponse.from(postService.createPost(
                request.content(),
                authorAuth0Id,
                authorName,
                jwt.getClaimAsString("picture")
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreatePostRequest(
            @NotBlank(message = "content is required")
            @Size(max = 140, message = "content must be at most 140 characters")
            String content
    ) {}
}
