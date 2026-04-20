package edu.eci.tdse.securetwitter.stream.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Stream Service API",
        version = "1.0",
        description = "Public stream of posts — no authentication required"
))
public class OpenApiConfig {
}
