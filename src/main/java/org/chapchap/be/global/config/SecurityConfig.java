package org.chapchap.be.global.config;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.auth.jwt.JwtAuthenticationFilter;
import org.chapchap.be.domain.auth.jwt.JwtProvider;
import org.chapchap.be.domain.user.service.UserService;
import org.chapchap.be.global.exception.CustomAccessDeniedHandler;
import org.chapchap.be.global.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UserService userService) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtProvider, userService);

        http
                .cors(cors -> {}) // 아래 CorsConfigurationSource 사용
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // 401
                        .accessDeniedHandler(accessDeniedHandler))          // 403
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration cfg = new org.springframework.web.cors.CorsConfiguration();

        // 프로덕션에서는 setAllowedOrigins에 실제 배포 도메인(예: https://app.example.com)만 넣고,
        // * 절대 금지(특히 allowCredentials(true)와 함께 쓰면 스펙 위반).
        cfg.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
        cfg.setAllowCredentials(true);

        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Content-Type","Authorization","X-Requested-With"));
        // 쿠키 받는 클라이언트가 헤더를 읽을 수 있도록 노출
        cfg.setExposedHeaders(java.util.List.of("Set-Cookie","Authorization"));

        // 쿠키 쓰면 Preflight가 자주 나가므로 캐시 조금
        cfg.setMaxAge(3600L);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}