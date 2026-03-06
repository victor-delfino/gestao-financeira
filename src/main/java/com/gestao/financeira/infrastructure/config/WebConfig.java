package com.gestao.financeira.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing).
 *
 * CAMADA: Infrastructure (infrastructure/config)
 * POR QUE EXISTE: O frontend React roda em http://localhost:5173 (Vite)
 *   e o backend em http://localhost:8080 (Spring Boot).
 *   Origens diferentes → o browser bloqueia requisições por padrão (CORS).
 *   Esta configuração libera o frontend para consumir a API.
 *
 * IMPORTANTE: Em produção, trocar allowedOrigins pelo domínio real
 *   (ex: "https://meuapp.com") — nunca usar "*" em produção com credenciais.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")          // aplica a todos os endpoints /api/*
                .allowedOrigins("http://localhost:5173")  // origem do React/Vite
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
