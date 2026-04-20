package edu.eci.tdse.securetwitter.user.repository;

import edu.eci.tdse.securetwitter.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuth0Id(String auth0Id);
}
