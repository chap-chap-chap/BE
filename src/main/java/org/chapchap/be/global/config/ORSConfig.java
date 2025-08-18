package org.chapchap.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ORSConfig {

    @Bean
    public WebClient orsRoutesClient(
            @Value("${app.routes.ors.base-url}") String baseUrl,
            @Value("${app.routes.ors.api-key}") String apiKey
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            // 설정은 읽혔지만 키가 비어있으면 명확한 메시지로 실패
            throw new IllegalStateException("ORS_API_KEY가 설정되지 않았습니다.");
        }

        return WebClient.builder()
                .baseUrl(baseUrl) // e.g. https://api.openrouteservice.org
                .defaultHeader("Authorization", apiKey) // ORS는 Bearer 없이 키만 넣는 형식
                .defaultHeader("Content-Type", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }
}