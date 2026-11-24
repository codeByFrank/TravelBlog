package org.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for Wikipedia (free, no API key). The base URL has a {language} part,
 * e.g. https://{language}.wikipedia.org; WikipediaService fills it in with the language of the user interface.
 */
@Configuration
public class WikipediaConfig {

    @Bean
    public RestClient wikipediaRestClient(@Value("${app.wikipedia.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
