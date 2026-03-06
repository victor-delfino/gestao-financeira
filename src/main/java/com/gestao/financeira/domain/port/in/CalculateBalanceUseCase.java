package com.gestao.financeira.domain.port.in;

import java.math.BigDecimal;

/**
 * Port de entrada: Caso de uso "Calcular Saldo".
 *
 * CAMADA: Domain (port/in)
 * POR QUE EXISTE: Define o contrato para calcular o saldo atual.
 *   Saldo = soma de receitas - soma de despesas.
 *   Quem chama: TransactionController (adapter in)
 *   Quem implementa: TransactionService (application)
 *
 * DECISÃO: Retorna BigDecimal (mesmo tipo do campo amount da Transaction).
 *   Pode ser negativo — significa que as despesas superam as receitas.
 */
public interface CalculateBalanceUseCase {

    /**
     * Calcula o saldo atual = total de receitas - total de despesas.
     *
     * @return saldo atual (pode ser negativo)
     */
    BigDecimal calculate();
}
