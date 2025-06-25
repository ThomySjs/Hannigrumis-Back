package com.Hannigrumis.api.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private String[] allowedOrigins;

    public CorsConfig() {
        this.allowedOrigins = loadOrigins();
    }

    public String[] loadOrigins() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        return dotenv.get("CORS_URLS").split(",");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
