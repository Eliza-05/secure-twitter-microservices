package edu.eci.tdse.securetwitter.controller;

import edu.eci.tdse.securetwitter.model.User;
import edu.eci.tdse.securetwitter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "Authenticated user info")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Object>> getMe(JwtAuthenticationToken token) {
        User user = userService.getOrCreateFromToken(token);

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", user.getAuth0Id());
        info.put("dbId", user.getId());
        info.put("email", user.getEmail());
        info.put("name", user.getName());
        info.put("picture", user.getPicture());

        return ResponseEntity.ok(info);
    }
}
