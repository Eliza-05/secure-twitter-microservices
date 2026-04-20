package edu.eci.tdse.securetwitter.user.controller;

import edu.eci.tdse.securetwitter.user.model.User;
import edu.eci.tdse.securetwitter.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "User", description = "Authenticated user profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Returns the profile of the user identified by the JWT. Creates the user on first access.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "User profile returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"id\":\"auth0|abc\",\"dbId\":1,\"email\":\"user@example.com\",\"name\":\"John Doe\",\"picture\":\"https://...\"}")))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content(mediaType = "application/json"))
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
