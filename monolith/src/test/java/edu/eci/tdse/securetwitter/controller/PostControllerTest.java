package edu.eci.tdse.securetwitter.controller;

import edu.eci.tdse.securetwitter.model.Post;
import edu.eci.tdse.securetwitter.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Test
    void shouldUseNameAndPictureClaimsWhenCreatingPost() {
        PostController controller = new PostController(postService);
        PostController.CreatePostRequest request = new PostController.CreatePostRequest("hello world");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "google-oauth2|123")
                .claim("name", "Sebastian Example")
                .claim("picture", "https://cdn.example/avatar.png")
                .build();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);

        Post saved = new Post();
        saved.setContent("hello world");
        saved.setAuthorAuth0Id("google-oauth2|123");
        saved.setAuthorName("Sebastian Example");
        saved.setAuthorPicture("https://cdn.example/avatar.png");

        when(postService.createPost(
                eq("hello world"),
                eq("google-oauth2|123"),
                eq("Sebastian Example"),
                eq("https://cdn.example/avatar.png")
        )).thenReturn(saved);

        ResponseEntity<Post> response = controller.createPost(request, token);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sebastian Example", response.getBody().getAuthorName());
        verify(postService).createPost(
                "hello world",
                "google-oauth2|123",
                "Sebastian Example",
                "https://cdn.example/avatar.png"
        );
    }

    @Test
    void shouldFallbackToSubWhenNameClaimIsMissing() {
        PostController controller = new PostController(postService);
        PostController.CreatePostRequest request = new PostController.CreatePostRequest("hello world");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "google-oauth2|fallback")
                .build();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);

        Post saved = new Post();
        saved.setContent("hello world");
        saved.setAuthorAuth0Id("google-oauth2|fallback");
        saved.setAuthorName("google-oauth2|fallback");

        when(postService.createPost(
                eq("hello world"),
                eq("google-oauth2|fallback"),
                eq("google-oauth2|fallback"),
                eq(null)
        )).thenReturn(saved);

        ResponseEntity<Post> response = controller.createPost(request, token);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(postService).createPost(
                "hello world",
                "google-oauth2|fallback",
                "google-oauth2|fallback",
                null
        );
    }
}

