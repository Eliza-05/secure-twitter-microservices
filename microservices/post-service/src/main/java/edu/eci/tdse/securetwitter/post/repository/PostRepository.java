package edu.eci.tdse.securetwitter.post.repository;

import edu.eci.tdse.securetwitter.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
