package com.gestao.financeira.domain.port.out;

import com.gestao.financeira.domain.model.Transaction;

import java.util.List;

/**
 * Port de saída: Contrato de persistência de transações.
 *
 * CAMADA: Domain (port/out)
 * POR QUE EXISTE: O domínio PRECISA persistir dados, mas não pode depender
 *   de JPA, PostgreSQL ou qualquer tecnologia de banco.
 *   Esta interface define O QUE o domínio precisa, sem dizer COMO.
 *
 *   Quem chama: TransactionService (application)
 *   Quem implementa: TransactionJpaAdapter (adapter out)
 *
 * DECISÃO ARQUITETURAL:
 *   Esta interface usa Transaction (classe de domínio) — não JPA Entity.
 *   O adapter de saída é RESPONSÁVEL por converter:
 *     Transaction (domínio) ↔ TransactionJpaEntity (JPA)
 *   O domínio NUNCA sabe que existe uma entidade JPA.
 *
 * INVERSÃO DE DEPENDÊNCIA (DIP):
 *   Sem esta interface: TransactionService → TransactionJpaAdapter
 *   Com esta interface: TransactionService → TransactionRepositoryPort ← TransactionJpaAdapter
 *   O Service depende da ABSTRAÇÃO, não da implementação concreta.
 */
public interface TransactionRepositoryPort {

    /**
     * Persiste uma transação e retorna a versão salva.
     *
     * @param transaction a transação a ser salva (pode ser nova ou existente)
     * @return a transação persistida
     */
    Transaction save(Transaction transaction);

    /**
     * Retorna todas as transações persistidas.
     *
     * @return lista de transações (pode ser vazia, nunca null)
     */
    List<Transaction> findAll();
}
