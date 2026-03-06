package com.gestao.financeira.domain.port.out;

/**
 * Port OUT — abstração para geração/validação de tokens JWT.
 *
 * CAMADA: Domain (porta de saída)
 * POR QUE EXISTE: O UserService precisa gerar tokens, mas NÃO deve
 *   conhecer a biblioteca JJWT diretamente. Esta interface permite
 *   que a implementação concreta (JwtTokenProvider) fique na camada
 *   de infraestrutura, respeitando a inversão de dependência.
 *
 * IMPLEMENTAÇÃO: infrastructure/security/JwtTokenProvider.java
 */
public interface TokenPort {

    /**
     * Gera um token JWT para o email fornecido.
     * O token contém: subject (email), data de emissão, expiração.
     */
    String generateToken(String email);

    /**
     * Extrai o email (subject) de um token JWT.
     * @throws RuntimeException se o token for inválido ou expirado
     */
    String extractEmail(String token);

    /**
     * Verifica se o token é válido (assinatura correta e não expirado).
     */
    boolean isTokenValid(String token);
}
