package org.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class FrontendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tripDetailPage_isForwardedToIndexHtml_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/trips/123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/index.html"));
    }

    @Test
    void wishlistPage_isForwardedToIndexHtml_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wishlist"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/index.html"));
    }

    @Test
    void statsPage_isForwardedToIndexHtml_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/stats"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/index.html"));
    }

    @Test
    void apiEndpoint_stillReturnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
