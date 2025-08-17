package org.chapchap.be.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "app.kakao.local")
public class KakaoLocalProperties {
    private String baseUrl;
    private String restApiKey;
}