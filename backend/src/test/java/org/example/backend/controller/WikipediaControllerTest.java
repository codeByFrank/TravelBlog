package org.example.backend.controller;

import org.example.backend.dto.WikipediaResponse;
import org.example.backend.service.JwtService;
import org.example.backend.service.WikipediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class WikipediaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    // The calls to Wikipedia are tested in WikipediaServiceTest; here only the endpoint
    @MockitoBean
    private WikipediaService wikipediaService;

    private static MockHttpServletRequestBuilder florence() {
        return MockMvcRequestBuilders.get("/api/wikipedia")
                .param("name", "Florenz")
                .param("latitude", "43.77")
                .param("longitude", "11.26");
    }

    @Test
    void getArticle_returnsArticleFromTheService() throws Exception {
        when(wikipediaService.find("Florenz", 43.77, 11.26, "de")).thenReturn(Optional.of(
                new WikipediaResponse("Florenz", "Florenz ist die Hauptstadt der Toskana.",
                        "https://de.wikipedia.org/wiki/Florenz")));

        mockMvc.perform(florence().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Florenz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("https://de.wikipedia.org/wiki/Florenz"));
    }

    @Test
    void getArticle_asksForTheEnglishArticle_whenTheUserInterfaceIsEnglish() throws Exception {
        when(wikipediaService.find("Florenz", 43.77, 11.26, "en")).thenReturn(Optional.of(
                new WikipediaResponse("Florence", "Florence is the capital city of Tuscany.",
                        "https://en.wikipedia.org/wiki/Florence")));

        mockMvc.perform(florence()
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Florence"));
    }

    @Test
    void getArticle_returnsNoContent_whenThereIsNoArticle() throws Exception {
        when(wikipediaService.find("Florenz", 43.77, 11.26, "de")).thenReturn(Optional.empty());

        mockMvc.perform(florence().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken("user-1")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getArticle_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(florence())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
