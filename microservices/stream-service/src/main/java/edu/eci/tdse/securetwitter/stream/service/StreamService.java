package edu.eci.tdse.securetwitter.stream.service;

import edu.eci.tdse.securetwitter.stream.model.Post;
import edu.eci.tdse.securetwitter.stream.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamService {

    private final PostRepository postRepository;

    public StreamService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getStream() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
