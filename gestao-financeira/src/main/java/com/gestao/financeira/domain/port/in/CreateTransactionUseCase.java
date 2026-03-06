package com.gestao.financeira.domain.port.in;

import com.gestao.financeira.application.service.CreateTransactionCommand;
import com.gestao.financeira.domain.model.Transaction;

/**
 * Port de entrada: Caso de uso "Criar Transação".
 *
 * CAMADA: Domain (port/in)
 * POR QUE EXISTE: Define o CONTRATO para criar uma transação.
 *   Quem chama: TransactionController (adapter in)
 *   Quem implementa: TransactionService (application)
 *
 * DECISÃO: Uma interface por caso de uso (ISP — Interface Segregation Principle).
 *   O Controller que só cria transações depende APENAS desta interface,
 *   não de uma interface gigante com 10 métodos.
 */
public interface CreateTransactionUseCase {

    /**
     * Executa o caso de uso de criação de uma transação.
     *
     * @param command dados necessários para criar a transação
     * @return a transação criada com ID gerado
     * @throws com.gestao.financeira.domain.exception.DomainException
     *         se os dados violarem regras de negócio
     */
    Transaction execute(CreateTransactionCommand command);
}
