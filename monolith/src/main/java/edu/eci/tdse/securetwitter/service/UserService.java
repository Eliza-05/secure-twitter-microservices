package edu.eci.tdse.securetwitter.service;

import edu.eci.tdse.securetwitter.model.User;
import edu.eci.tdse.securetwitter.repository.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateFromToken(JwtAuthenticationToken token) {
        String auth0Id = token.getName();

        return userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> userRepository.save(buildUserFromToken(token, auth0Id)));
    }

    private User buildUserFromToken(JwtAuthenticationToken token, String auth0Id) {
        User user = new User();
        user.setAuth0Id(auth0Id);
        user.setEmail((String) token.getTokenAttributes().getOrDefault("email", ""));
        user.setName((String) token.getTokenAttributes().getOrDefault("name", auth0Id));
        user.setPicture((String) token.getTokenAttributes().get("picture"));
        return user;
    }
}

