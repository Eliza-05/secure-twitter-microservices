package edu.eci.tdse.securetwitter.post.service;

import edu.eci.tdse.securetwitter.post.model.Post;
import edu.eci.tdse.securetwitter.post.model.UserProfile;
import edu.eci.tdse.securetwitter.post.repository.PostRepository;
import edu.eci.tdse.securetwitter.post.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private static final int MAX_LENGTH = 140;

    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;

    public PostService(PostRepository postRepository, UserProfileRepository userProfileRepository) {
        this.postRepository = postRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public Post createPost(String content, String authorAuth0Id, String authorName, String authorPicture) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (content.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Post content must be at most 140 characters");
        }

        UserProfile userProfile = userProfileRepository.findByAuth0Id(authorAuth0Id).orElse(null);
        String resolvedAuthorName = resolveAuthorName(authorAuth0Id, authorName, userProfile);
        String resolvedAuthorPicture = resolveAuthorPicture(authorPicture, userProfile);

        Post post = new Post();
        post.setContent(content.trim());
        post.setAuthorAuth0Id(authorAuth0Id);
        post.setAuthorName(resolvedAuthorName);
        post.setAuthorPicture(resolvedAuthorPicture);
        return postRepository.save(post);
    }

    private String resolveAuthorName(String authorAuth0Id, String authorName, UserProfile userProfile) {
        if (hasText(authorName) && !authorAuth0Id.equals(authorName)) {
            return authorName.trim();
        }
        if (userProfile != null && hasText(userProfile.getName())) {
            return userProfile.getName().trim();
        }
        return authorAuth0Id;
    }

    private String resolveAuthorPicture(String authorPicture, UserProfile userProfile) {
        if (hasText(authorPicture)) {
            return authorPicture.trim();
        }
        if (userProfile != null && hasText(userProfile.getPicture())) {
            return userProfile.getPicture().trim();
        }
        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
