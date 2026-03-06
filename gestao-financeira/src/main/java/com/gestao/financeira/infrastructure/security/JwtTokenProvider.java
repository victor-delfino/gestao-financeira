package com.gestao.financeira.infrastructure.security;

import com.gestao.financeira.domain.port.out.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Implementação concreta do TokenPort usando a biblioteca JJWT.
 *
 * CAMADA: Infrastructure (security)
 * IMPLEMENTA: TokenPort (domain port out)
 *
 * ═══════════════════════════════════════════════════════════════
 *  COMO FUNCIONA UM JWT
 * ═══════════════════════════════════════════════════════════════
 *
 *  Um JWT é composto por 3 partes separadas por pontos:
 *
 *  HEADER.PAYLOAD.SIGNATURE
 *  eyJhbGci...  eyJzdWIi...  SflKxwRJ...
 *
 *  1. HEADER: algoritmo de assinatura (HS256, RS256, etc.)
 *  2. PAYLOAD (Claims): dados do usuário + metadados
 *     - sub (subject): email do usuário
 *     - iat (issued at): quando foi criado
 *     - exp (expiration): quando expira
 *  3. SIGNATURE: HMAC-SHA256(header + payload, SECRET_KEY)
 *     É a "garantia" de que o token não foi adulterado.
 *     Se alguém mudar o payload, a assinatura não bate mais.
 *
 *  O token NÃO é criptografado — qualquer um pode ler o payload
 *  (basta decodificar Base64). A assinatura apenas GARANTE que
 *  o token foi emitido por este servidor e não foi alterado.
 *
 *  NUNCA coloque dados sensíveis (senha, CPF) no payload!
 *
 * ═══════════════════════════════════════════════════════════════
 *  CONFIGURAÇÃO
 * ═══════════════════════════════════════════════════════════════
 *
 *  @Value("${jwt.secret}"): lê a chave secreta do application.yml
 *  @Value("${jwt.expiration}"): lê o tempo de expiração em milissegundos
 *
 *  A chave secreta DEVE ter pelo menos 256 bits (32 bytes) para HMAC-SHA256.
 *  Em produção, use variável de ambiente, nunca hardcode.
 */
@Component
public class JwtTokenProvider implements TokenPort {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {
        // Decodifica a chave Base64 para bytes e cria a SecretKey HMAC
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Gera um token JWT para o email fornecido.
     *
     * O token contém:
     * - subject: email do usuário
     * - issuedAt: momento da criação
     * - expiration: momento da expiração (issuedAt + expirationMs)
     * - assinatura: HMAC-SHA256 com nossa chave secreta
     */
    @Override
    public String generateToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(email)             // quem é o dono do token
            .issuedAt(now)              // quando foi criado
            .expiration(expiration)     // quando expira
            .signWith(secretKey)        // assina com a chave secreta
            .compact();                 // serializa para String
    }

    /**
     * Extrai o email (subject) do token.
     *
     * parseSignedClaims() faz 3 coisas:
     * 1. Decodifica o Base64
     * 2. Verifica a assinatura com a secretKey
     * 3. Verifica se o token não está expirado
     *
     * Se qualquer verificação falhar, lança exceção.
     */
    @Override
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    /**
     * Verifica se o token é válido.
     * Tenta parsear e verificar — se não lançar exceção, é válido.
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token inválido, expirado, assinatura incorreta, etc.
            return false;
        }
    }
}
