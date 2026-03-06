package com.gestao.financeira.adapters.out.persistence.entity;

import com.gestao.financeira.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade JPA para persistência de transações.
 *
 * CAMADA: Adapter OUT (adapters/out/persistence/entity)
 * POR QUE EXISTE: Representa a estrutura da tabela no banco de dados.
 *   É propositalmente DIFERENTE da entidade de domínio Transaction.
 *   Enquanto Transaction carrega regras de negócio, esta classe
 *   só sabe falar com o Hibernate/PostgreSQL.
 *
 * DECISÕES:
 *   - @Entity/@Table: mapeamento JPA para a tabela "transactions"
 *   - Construtor vazio: exigido pelo Hibernate para instanciar objetos
 *   - Sem validações: dados chegam sempre válidos (vindos do domínio)
 *   - UUID como ID: identificador globalmente único, sem sequência numérica
 *     exposta na API (mais seguro que Long auto-increment)
 *   - @Enumerated(STRING): salva "INCOME"/"EXPENSE" como texto no banco,
 *     não como número (0/1) — muito mais legível em queries SQL diretas
 *
 * DEPENDÊNCIAS PERMITIDAS: jakarta.persistence, domain.model (para o enum)
 */
@Entity
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)    // salva "INCOME" ou "EXPENSE" como texto
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    // ── Exigido pelo Hibernate para instanciar entidades ──
    protected TransactionJpaEntity() {}

    public TransactionJpaEntity(UUID id,
                                 String description,
                                 BigDecimal amount,
                                 TransactionType type,
                                 String category,
                                 LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public UUID getId()               { return id; }
    public String getDescription()    { return description; }
    public BigDecimal getAmount()     { return amount; }
    public TransactionType getType()  { return type; }
    public String getCategory()       { return category; }
    public LocalDate getDate()        { return date; }
}
