package edu.eci.tdse.securetwitter.stream.repository;

import edu.eci.tdse.securetwitter.stream.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
}
