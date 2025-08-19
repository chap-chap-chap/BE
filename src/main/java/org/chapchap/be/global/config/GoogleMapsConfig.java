package org.chapchap.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleMapsConfig {

    @Bean(name = "googlePlacesClient")
    public WebClient googleMapsClient(
            @Value("${app.google.places.base-url}") String baseUrl,
            @Value("${app.google.places.api-key}") String apiKey
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GOOGLE_MAPS_API_KEY가 설정되지 않았습니다.");
        }
        return WebClient.builder()
                .baseUrl(baseUrl)
                // Places API v1은 Authorization이 아닌 전용 헤더를 씁니다.
                .defaultHeader("X-Goog-Api-Key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                        .build())
                .build();
    }
}
