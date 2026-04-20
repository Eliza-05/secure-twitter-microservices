package edu.eci.tdse.securetwitter.service;

import edu.eci.tdse.securetwitter.model.Post;
import edu.eci.tdse.securetwitter.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldRejectPostLongerThan140Characters() {
        String tooLong = "x".repeat(141);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(tooLong, "auth0|user", "User", null)
        );

        assertEquals("Post content must be at most 140 characters", exception.getMessage());
    }

    @Test
    void shouldCreatePostWithValidLength() {
        String valid = "x".repeat(140);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post post = postService.createPost(valid, "auth0|user", "User", null);

        assertEquals(140, post.getContent().length());
        assertEquals("auth0|user", post.getAuthorAuth0Id());
    }
}
