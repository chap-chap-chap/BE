package org.chapchap.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class GoogleRoutesConfig {

    @Bean
    public WebClient googleRoutesClient(
            @Value("${app.google.routes.base-url}") String baseUrl,
            @Value("${app.google.routes.api-key}") String apiKey
    ) {
        // 타임아웃/버퍼 등 운영 기본값
        HttpClient http = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        return WebClient.builder()
                .baseUrl(baseUrl)
                // API 키는 공통 헤더로
                .defaultHeader("X-Goog-Api-Key", apiKey)
                // 응답 본문이 꽤 클 수 있어 버퍼 넉넉히
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();
    }
}