package org.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for the German Wikipedia (free, no API key).
 */
@Configuration
public class WikipediaConfig {

    @Bean
    public RestClient wikipediaRestClient(@Value("${app.wikipedia.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
