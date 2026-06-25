package com.esgis2026.assigame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // Dossier local où sont stockées les images uploadées (voir UploadController)
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // URL publique du frontend en production (ex: https://assigame.vercel.app), définie via
    // la variable d'environnement FRONTEND_URL. En local, http://localhost:3000 est déjà autorisé.
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://127.0.0.1:5500",
                                "http://localhost:*",
                                "http://127.0.0.1:*",
                                frontendUrl
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Expose le dossier d'upload sous /uploads/** afin que les images des produits
                // et des photos de profil soient accessibles directement par le frontend.
                String location = "file:" + uploadDir + "/";
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations(location);
            }
        };
    }
}
