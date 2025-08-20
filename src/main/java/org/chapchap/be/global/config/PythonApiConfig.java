package org.chapchap.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PythonApiConfig {

    @Bean
    public PythonApiClient pythonApiClient(@Value("${app.python.base-url}") String baseUrl) {
        return new PythonApiClient(baseUrl);
    }
}
