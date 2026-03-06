package com.gestao.financeira.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade JPA para a tabela "users".
 *
 * CAMADA: Adapter OUT (persistência)
 * POR QUE EXISTE: Mapeia o domínio User para o banco de dados.
 *   Assim como TransactionJpaEntity, esta classe é separada da
 *   entidade de domínio User.java para manter o domínio puro.
 *
 * ATENÇÃO: o campo "email" tem @Column(unique = true) para garantir
 *   no nível do banco que não há emails duplicados.
 *   O UserService TAMBÉM verifica antes de salvar (defesa em profundidade).
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Construtor protegido para o Hibernate
    protected UserJpaEntity() {}

    public UserJpaEntity(UUID id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // ─── Getters ─────────────────────────────────────────────────────
    public UUID getId()         { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
