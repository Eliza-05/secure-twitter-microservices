package edu.eci.tdse.securetwitter.service;

import edu.eci.tdse.securetwitter.model.Post;
import edu.eci.tdse.securetwitter.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private static final int MAX_POST_LENGTH = 140;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getStream() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post createPost(String content, String authorAuth0Id, String authorName, String authorPicture) {
        validatePostContent(content);

        Post post = new Post();
        post.setContent(content.trim());
        post.setAuthorAuth0Id(authorAuth0Id);
        post.setAuthorName(authorName);
        post.setAuthorPicture(authorPicture);

        return postRepository.save(post);
    }

    void validatePostContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        if (content.trim().length() > MAX_POST_LENGTH) {
            throw new IllegalArgumentException("Post content must be at most 140 characters");
        }
    }
}

