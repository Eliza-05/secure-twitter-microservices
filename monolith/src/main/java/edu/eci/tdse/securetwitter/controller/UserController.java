package edu.eci.tdse.securetwitter.controller;

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

    @GetMapping("/me")
    @Operation(summary = "Get current user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Object>> getMe(JwtAuthenticationToken token) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", token.getName());
        info.put("email", token.getTokenAttributes().get("email"));
        info.put("name", token.getTokenAttributes().get("name"));
        info.put("picture", token.getTokenAttributes().get("picture"));
        return ResponseEntity.ok(info);
    }
}
