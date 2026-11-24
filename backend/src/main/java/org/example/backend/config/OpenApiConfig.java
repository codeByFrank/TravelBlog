package org.example.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title and login of the API documentation (Swagger UI at /swagger-ui.html).
 * springdoc finds the endpoints in the controllers by itself; this only adds what it cannot know:
 * a description, and that the endpoints need the JWT from POST /api/auth/login in the Authorization header.
 */
@Configuration
public class OpenApiConfig {

    private static final String JWT_SCHEME = "JWT";

    @Bean
    public OpenAPI travelBlogApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TravelBlog API")
                        .version("1.0")
                        .description("""
                                REST API of TravelBlog, a travel diary on a map: trips with stops and photos, \
                                the wishlist, the place search and the first sentences from Wikipedia.

                                To try an endpoint: log in with POST /api/auth/login (e.g. the demo user from the \
                                README), copy the token from the answer, click "Authorize" and paste it. \
                                Every user sees and changes only their own data."""))
                // Requests from Swagger UI go to the address the page was loaded from. Without this, springdoc
                // would guess the address and write http:// behind Render's https proxy, so "Try it out" would fail.
                .addServersItem(new Server().url("/").description("This server"))
                // The button "Authorize" asks for the token and sends it as "Authorization: Bearer <token>"
                .components(new Components().addSecuritySchemes(JWT_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME));
    }
}
