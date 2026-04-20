package edu.eci.tdse.securetwitter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Prevents the app from calling Auth0 JWKS endpoint on startup
    @MockBean
    private JwtDecoder jwtDecoder;

    // ─── GET /api/stream ──────────────────────────────────────────────────────

    @Test
    void getStream_publicEndpoint_returns200WithJsonArray() throws Exception {
        mockMvc.perform(get("/api/stream"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPosts_publicEndpoint_returns200WithJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // ─── POST /api/posts ──────────────────────────────────────────────────────

    @Test
    void createPost_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"hello\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_withValidToken_returns201AndPost() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j
                                .claim("sub", "auth0|integrationtest")
                                .claim("name", "Integration User")
                                .claim("picture", "https://example.com/pic.png")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hello from integration test!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("Hello from integration test!")))
                .andExpect(jsonPath("$.authorName", is("Integration User")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void createPost_contentExceeds140Chars_returns400() throws Exception {
        String tooLong = "x".repeat(141);
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.claim("sub", "auth0|integrationtest")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_emptyContent_returns400() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.claim("sub", "auth0|integrationtest")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_missingNameClaim_usesSubAsFallbackAuthorName() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.claim("sub", "auth0|noname")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Post without name claim\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorName", is("auth0|noname")));
    }

    @Test
    void createPost_streamContainsNewPost_afterCreation() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j
                                .claim("sub", "auth0|streamcheck")
                                .claim("name", "Stream Checker")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Visible in stream\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/stream"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.content == 'Visible in stream')]", hasSize(1)));
    }

    // ─── GET /api/me ──────────────────────────────────────────────────────────

    @Test
    void getMe_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMe_withValidToken_returns200WithUserInfo() throws Exception {
        mockMvc.perform(get("/api/me")
                        .with(jwt().jwt(j -> j
                                .claim("sub", "auth0|metest")
                                .claim("email", "me@example.com")
                                .claim("name", "Me User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("auth0|metest")))
                .andExpect(jsonPath("$.email", is("me@example.com")))
                .andExpect(jsonPath("$.name", is("Me User")));
    }

    @Test
    void getMe_calledTwiceWithSameToken_doesNotDuplicateUser() throws Exception {
        mockMvc.perform(get("/api/me")
                        .with(jwt().jwt(j -> j
                                .claim("sub", "auth0|dedup")
                                .claim("email", "dedup@example.com")
                                .claim("name", "Dedup User"))))
                .andExpect(status().isOk());

        // Second call with same auth0 id must still return 200 (not fail with duplicate key)
        mockMvc.perform(get("/api/me")
                        .with(jwt().jwt(j -> j
                                .claim("sub", "auth0|dedup")
                                .claim("email", "dedup@example.com")
                                .claim("name", "Dedup User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("auth0|dedup")));
    }
}
