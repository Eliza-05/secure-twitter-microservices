package edu.eci.tdse.securetwitter.dto;

import edu.eci.tdse.securetwitter.model.Post;

import java.time.Instant;

public record PostResponse(
        Long id,
        String content,
        String authorName,
        String authorPicture,
        Instant createdAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getAuthorName(),
                post.getAuthorPicture(),
                post.getCreatedAt()
        );
    }
}
