package org.chapchap.be.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(KakaoLocalProperties.class)
@RequiredArgsConstructor
public class KakaoLocalConfig {

    private final KakaoLocalProperties props;

    @Bean
    public RestClient kakaoRestClient() {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + props.getRestApiKey())
                .build();
    }
}