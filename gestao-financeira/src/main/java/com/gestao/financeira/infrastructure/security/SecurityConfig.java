package com.gestao.financeira.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração central do Spring Security.
 *
 * CAMADA: Infrastructure
 *
 * ═══════════════════════════════════════════════════════════════
 *  O QUE CADA CONFIGURAÇÃO FAZ
 * ═══════════════════════════════════════════════════════════════
 *
 *  csrf().disable():
 *    CSRF (Cross-Site Request Forgery) é uma proteção para
 *    aplicações que usam sessão/cookies. Como usamos JWT (stateless),
 *    não precisamos de CSRF. Cada request carrega o token no header.
 *
 *  sessionManagement().sessionCreationPolicy(STATELESS):
 *    Diz ao Spring para NÃO criar sessão HTTP (JSESSIONID).
 *    Cada request é independente — autenticação vem do token JWT.
 *    Isso torna a API escalável (sem estado no servidor).
 *
 *  authorizeHttpRequests():
 *    Define quais endpoints são públicos e quais exigem autenticação.
 *    - /api/auth/** → público (login e registro)
 *    - OPTIONS → público (preflight CORS do navegador)
 *    - Todo o resto → precisa de JWT válido
 *
 *  addFilterBefore():
 *    Insere nosso JwtAuthenticationFilter ANTES do filtro padrão
 *    do Spring Security. Assim, o JWT é processado primeiro.
 *
 *  cors():
 *    Habilita CORS para permitir requests do frontend React (localhost:5173).
 *    Substituímos o WebConfig anterior — o Security gerencia CORS agora.
 *
 *  BCryptPasswordEncoder:
 *    Algoritmo de hash para senhas. BCrypt é intencionalmente LENTO
 *    (10 rounds por padrão), o que dificulta ataques de força bruta.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura a cadeia de filtros de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilita CSRF (não usamos sessão, usamos JWT)
            .csrf(csrf -> csrf.disable())

            // 2. Habilita CORS com nossa configuração
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. Sem sessão — cada request é independente
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. Regras de autorização
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos — qualquer um pode acessar
                .requestMatchers("/api/auth/**").permitAll()
                // Preflight CORS (o navegador envia OPTIONS antes de POST/PUT/DELETE)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Todo o resto exige autenticação
                .anyRequest().authenticated()
            )

            // 5. Insere nosso filtro JWT antes do filtro padrão
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Configuração de CORS centralizada no Security.
     *
     * Antes tínhamos WebConfig com addCorsMappings.
     * Quando o Spring Security está ativo, ele SOBRESCREVE a config do WebMvc.
     * Por isso movemos a config de CORS para cá.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    /**
     * Bean do BCryptPasswordEncoder.
     * Será usado pelo adapter que implementa PasswordEncoderPort.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
