package edu.eci.tdse.securetwitter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 140, nullable = false)
    private String content;

    @Column(name = "author_auth0_id", nullable = false)
    private String authorAuth0Id;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_picture")
    private String authorPicture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorAuth0Id() {
        return authorAuth0Id;
    }

    public void setAuthorAuth0Id(String authorAuth0Id) {
        this.authorAuth0Id = authorAuth0Id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPicture() {
        return authorPicture;
    }

    public void setAuthorPicture(String authorPicture) {
        this.authorPicture = authorPicture;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

