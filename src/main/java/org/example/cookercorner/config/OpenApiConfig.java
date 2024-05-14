package org.example.cookercorner.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Akzhol",
                        email = "akzholbek0096@gmail.com",
                        url = "https://t.me/akzholbek"
                ),
                title = "Cooker corner mobile app",
                description = "OpenApi documentation for Cooker Corner Spring Project",
                version = "0.0.1"
        ),
        servers = {
                @Server(
                        description = "Local environment",
                        url = "https://supportive-love-cook2.up.railway.app"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
