package edu.eci.tdse.securetwitter.service;

import edu.eci.tdse.securetwitter.model.User;
import edu.eci.tdse.securetwitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private JwtAuthenticationToken buildToken(String sub, String name, String email) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", sub)
                .claim("name", name)
                .claim("email", email)
                .build();
        return new JwtAuthenticationToken(jwt);
    }

    @Test
    void getOrCreate_existingUser_returnsExistingUserWithoutSaving() {
        User existing = new User();
        existing.setAuth0Id("auth0|existing");
        existing.setName("Existing User");
        existing.setEmail("existing@example.com");

        when(userRepository.findByAuth0Id("auth0|existing")).thenReturn(Optional.of(existing));

        JwtAuthenticationToken token = buildToken("auth0|existing", "Existing User", "existing@example.com");
        User result = userService.getOrCreateFromToken(token);

        assertEquals("auth0|existing", result.getAuth0Id());
        assertEquals("Existing User", result.getName());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getOrCreate_newUser_savesUserWithClaimsFromToken() {
        when(userRepository.findByAuth0Id("auth0|newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        JwtAuthenticationToken token = buildToken("auth0|newuser", "New User", "new@example.com");
        User result = userService.getOrCreateFromToken(token);

        assertEquals("auth0|newuser", result.getAuth0Id());
        assertEquals("New User", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getOrCreate_missingNameClaim_usesAuth0IdAsName() {
        when(userRepository.findByAuth0Id("auth0|noname")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "auth0|noname")
                .claim("email", "noname@example.com")
                .build();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);

        User result = userService.getOrCreateFromToken(token);

        assertEquals("auth0|noname", result.getName());
    }

    @Test
    void getOrCreate_missingEmailClaim_savesWithEmptyEmail() {
        when(userRepository.findByAuth0Id("auth0|noemail")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "auth0|noemail")
                .claim("name", "No Email User")
                .build();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);

        User result = userService.getOrCreateFromToken(token);

        assertEquals("", result.getEmail());
    }
}
