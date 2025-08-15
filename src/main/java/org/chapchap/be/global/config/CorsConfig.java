package org.chapchap.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 개발용: 로컬 프론트 허용
        cfg.setAllowedOrigins(List.of(
                "https://shallwewalk.kro.kr",
                "https://www.shallwewalk.kro.kr",
                "http://localhost:3000"
        ));

        // 인증 쿠키/자격증명 허용 (프론트 fetch/XHR에 credentials: 'include' 필요)
        cfg.setAllowCredentials(true);

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With"));
        // 클라이언트가 읽을 수 있게 노출할 헤더
        cfg.setExposedHeaders(List.of("Set-Cookie","Authorization"));
        // preflight 캐시
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
