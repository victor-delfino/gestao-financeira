package com.gestao.financeira.domain.model;

/**
 * Tipo de transação financeira.
 *
 * CAMADA: Domain
 * POR QUE EXISTE: Representa um valor do negócio com semântica clara.
 *   Usar String "INCOME"/"EXPENSE" seria frágil (typos, valores inválidos).
 *   Um enum garante que só existem exatamente dois estados possíveis.
 *
 * DEPENDÊNCIAS PERMITIDAS: nenhuma (é um enum Java puro)
 */
public enum TransactionType {

    INCOME,
    EXPENSE
}
