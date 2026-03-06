package com.gestao.financeira.domain.model;

import com.gestao.financeira.domain.exception.DomainException;

import java.util.UUID;

/**
 * Entidade de domínio que representa um usuário do sistema.
 *
 * CAMADA: Domain
 * POR QUE EXISTE: Encapsula as regras de negócio de um usuário.
 *   Assim como Transaction, esta classe NÃO tem anotações JPA.
 *   A entidade JPA (UserJpaEntity) existe separada em adapters/out.
 *
 * DECISÕES DE DESIGN:
 *   - O campo "password" armazena o HASH BCrypt, nunca a senha em texto puro.
 *     Quem faz o hash é o UserService (application layer), não o domínio.
 *     O domínio apenas garante que o hash não é nulo/vazio.
 *
 *   - Sem roles/permissões por enquanto: simplificamos para focar no JWT.
 *     Em um sistema real, teríamos Set<Role> ou List<String> authorities.
 *
 *   - Imutável após construção (mesma filosofia de Transaction).
 */
public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final String password; // hash BCrypt — NUNCA texto puro

    /**
     * Construtor para NOVO usuário (ID gerado automaticamente).
     */
    public User(String name, String email, String password) {
        this(UUID.randomUUID(), name, email, password);
    }

    /**
     * Construtor para RECONSTRUIR usuário do banco de dados.
     */
    public User(UUID id, String name, String email, String password) {
        if (name == null || name.isBlank()) {
            throw new DomainException("O nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new DomainException("O email é obrigatório.");
        }
        if (!email.contains("@")) {
            throw new DomainException("O email deve ser válido.");
        }
        if (password == null || password.isBlank()) {
            throw new DomainException("A senha é obrigatória.");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // ─── Getters (sem setters — imutável) ────────────────────────────
    public UUID getId()        { return id; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getPassword(){ return password; }
}
