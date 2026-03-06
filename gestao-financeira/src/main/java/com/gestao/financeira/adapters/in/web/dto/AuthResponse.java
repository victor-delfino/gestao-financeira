package com.gestao.financeira.adapters.in.web.dto;

/**
 * DTO de resposta para autenticação.
 *
 * CAMADA: Adapter IN (web/dto)
 *
 * Retornado após login bem-sucedido.
 * Contém o token JWT que o frontend deve armazenar
 * e enviar em todas as requisições subsequentes.
 *
 * Também retorna nome e email para o frontend exibir sem uma segunda requisição.
 */
public class AuthResponse {

    private final String token;
    private final String name;
    private final String email;

    public AuthResponse(String token, String name, String email) {
        this.token = token;
        this.name = name;
        this.email = email;
    }

    public String getToken() { return token; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
}
