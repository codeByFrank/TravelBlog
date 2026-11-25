package org.example.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;

// The health check (Actuator) and the API documentation (springdoc) are reachable without a token
@SpringBootTest
@AutoConfigureMockMvc
class HealthAndApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_isUp_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"))
                // No details about the database for visitors
                .andExpect(MockMvcResultMatchers.jsonPath("$.components").doesNotExist());
    }

    @Test
    void otherActuatorEndpoints_areClosed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/env"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void apiDocs_describeTheApiWithJwtLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.info.title").value("TravelBlog API"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/api/trips']").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/api/auth/login']").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.components.securitySchemes.JWT.scheme").value("bearer"))
                // Relative, so it works behind Render's https proxy as well as on localhost
                .andExpect(MockMvcResultMatchers.jsonPath("$.servers[0].url").value("/"));
    }

    @Test
    void apiDocs_leaveOutThePagesOfTheFrontend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/trips']").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/']").doesNotExist());
    }

    @Test
    void swaggerUi_isReachable_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/index.html"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Swagger UI")));
    }
}
