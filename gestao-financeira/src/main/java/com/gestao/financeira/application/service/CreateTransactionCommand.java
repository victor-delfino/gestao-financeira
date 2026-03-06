package com.gestao.financeira.application.service;

import com.gestao.financeira.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Comando para criação de uma nova transação.
 *
 * CAMADA: Application
 * POR QUE EXISTE: Transporta os dados necessários para criar uma Transaction,
 *   sem expor a estrutura dos DTOs da camada web para a camada Application.
 *   É o "formulário interno" que o Use Case recebe.
 *
 * DECISÃO: Classe com construtor + getters
 *
 * DEPENDÊNCIAS PERMITIDAS: domain.model (para TransactionType)
 */
public class CreateTransactionCommand {

    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;
    private final LocalDate date;

    public CreateTransactionCommand(String description,
                                    BigDecimal amount,
                                    TransactionType type,
                                    String category,
                                    LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public String getDescription() { return description; }
    public BigDecimal getAmount()   { return amount; }
    public TransactionType getType() { return type; }
    public String getCategory()     { return category; }
    public LocalDate getDate()      { return date; }
}
