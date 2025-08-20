package org.chapchap.be.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 컨테이너 내부 저장 경로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 필요 시 허용 도메인 지정
        registry.addMapping("/**")
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS");
    }
}
