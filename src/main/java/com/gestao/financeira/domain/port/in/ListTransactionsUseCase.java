package com.gestao.financeira.domain.port.in;

import com.gestao.financeira.domain.model.Transaction;

import java.util.List;

/**
 * Port de entrada: Caso de uso "Listar Transações".
 *
 * CAMADA: Domain (port/in)
 * POR QUE EXISTE: Define o contrato para listar todas as transações.
 *   Quem chama: TransactionController (adapter in)
 *   Quem implementa: TransactionService (application)
 */
public interface ListTransactionsUseCase {

    /**
     * Retorna todas as transações registradas no sistema.
     *
     * @return lista de transações (pode ser vazia, nunca null)
     */
    List<Transaction> execute();
}
