package edu.eci.tdse.securetwitter.post.controller;

import edu.eci.tdse.securetwitter.post.dto.PostResponse;
import edu.eci.tdse.securetwitter.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Posts", description = "Create posts — requires Auth0 JWT")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    @Operation(
            summary = "Create a new post",
            description = "Creates a post of up to 140 characters. Requires a valid Auth0 JWT Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Post created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<PostResponse> createPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
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
        String authorName = firstNonBlank(
                request.authorName(),
                jwt.getClaimAsString("name"),
                authorAuth0Id
        );
        String authorPicture = firstNonBlank(
                request.authorPicture(),
                jwt.getClaimAsString("picture"),
                null
        );

        PostResponse response = PostResponse.from(postService.createPost(
                request.content(),
                authorAuth0Id,
                authorName,
                authorPicture
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreatePostRequest(
            @NotBlank(message = "content is required")
            @Size(max = 140, message = "content must be at most 140 characters")
            String content,
            String authorName,
            String authorPicture
    ) {}

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
