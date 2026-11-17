package org.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for Nominatim, the place search of OpenStreetMap (free, no API key).
 */
@Configuration
public class NominatimConfig {

    @Bean
    public RestClient nominatimRestClient(@Value("${app.nominatim.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
