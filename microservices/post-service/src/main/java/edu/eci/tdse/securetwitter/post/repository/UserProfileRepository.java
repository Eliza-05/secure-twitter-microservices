package edu.eci.tdse.securetwitter.post.repository;

import edu.eci.tdse.securetwitter.post.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByAuth0Id(String auth0Id);
}
