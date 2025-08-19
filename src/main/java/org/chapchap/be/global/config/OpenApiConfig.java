package org.chapchap.be.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER = "bearerAuth";
    public static final String COOKIE = "cookieAuth";

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
                // Bearer: Authorization: Bearer <JWT>
                .addSecuritySchemes(BEARER, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Paste **access token only** (no 'Bearer ' prefix)"))
                // Cookie: ACCESS_TOKEN 쿠키 사용
                .addSecuritySchemes(COOKIE, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("ACCESS_TOKEN")
                        .description("ACCESS_TOKEN cookie"));

        return new OpenAPI()
                .info(new Info()
                        .title("Chap-Chap API")
                        .version("v1")
                        .description("Auth(JWT: Bearer/Cookie) + Sample endpoints"))
                .components(components)
                // 배포/로컬 서버 목록
                .servers(List.of(
                        new Server().url("https://shallwewalk.kro.kr").description("Prod"),
                        new Server().url("http://localhost:8078").description("Local")
                ))
                // 전역 SecurityRequirement (인증 헤더/쿠키 중 하나 충족하면 통과)
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .addSecurityItem(new SecurityRequirement().addList(COOKIE));
    }

    /**
     * 선택) 기본값으로 모든 operation에 보안 요구사항 삽입하고 싶을 때 커스터마이저 사용
     * 개별 @Operation(security=...)를 매번 안 적어도 됨.
     */
    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(op -> {
                    // 이미 명시된 경우 중복 추가 안 함
                    boolean hasSecurity = op.getSecurity() != null && !op.getSecurity().isEmpty();
                    if (!hasSecurity) {
                        op.addSecurityItem(new SecurityRequirement().addList(BEARER));
                        op.addSecurityItem(new SecurityRequirement().addList(COOKIE));
                    }
                })
        );
    }
}
