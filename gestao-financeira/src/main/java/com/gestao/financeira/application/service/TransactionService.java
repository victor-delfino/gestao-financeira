package com.gestao.financeira.application.service;

import com.gestao.financeira.domain.model.Transaction;
import com.gestao.financeira.domain.model.TransactionType;
import com.gestao.financeira.domain.port.in.CalculateBalanceUseCase;
import com.gestao.financeira.domain.port.in.CreateTransactionUseCase;
import com.gestao.financeira.domain.port.in.ListTransactionsUseCase;
import com.gestao.financeira.domain.port.out.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implementação dos casos de uso de transação financeira.
 *
 * CAMADA: Application
 * POR QUE EXISTE: Orquestra a lógica de negócio para os três casos de uso
 *   relacionados a Transaction. É o "maestro" que coordena domínio e repositório.
 *
 * IMPLEMENTA (ports de entrada):
 *   - CreateTransactionUseCase  → cria e persiste uma transação
 *   - ListTransactionsUseCase   → lista todas as transações
 *   - CalculateBalanceUseCase   → calcula receitas - despesas
 *
 * USA (port de saída):
 *   - TransactionRepositoryPort → interface de persistência
 *     (não sabe se por trás é JPA, MongoDB, memória, etc.)
 *
 * NÃO USA:
 *   - Nenhuma annotation do Spring (@Service, @Autowired, etc.)
 *   - O wiring (injeção de dependência) é feito pela Infrastructure (BeanConfiguration)
 *   - Isso mantém o Application testável sem contexto Spring
 *
 * DEPENDÊNCIAS PERMITIDAS: domain.model, domain.port, application.service
 */
public class TransactionService
        implements CreateTransactionUseCase,
                   ListTransactionsUseCase,
                   CalculateBalanceUseCase {

    // =========================================================
    // DEPENDÊNCIA via Port de saída (interface — não implementação)
    // O Service não sabe que por trás existe um JpaAdapter.
    // =========================================================
    private final TransactionRepositoryPort repository;

    /**
     * Construtor com injeção de dependência.
     *
     * Recebe a interface, nunca a implementação concreta.
     * O Spring (via BeanConfiguration) vai injetar o JpaAdapter aqui,
     * mas o Service não sabe — e não precisa saber.
     */
    public TransactionService(TransactionRepositoryPort repository) {
        this.repository = repository;
    }

    // =========================================================
    // CASO DE USO 1: Criar Transação
    // =========================================================

    /**
     * Cria e persiste uma nova transação financeira.
     *
     * FLUXO:
     *   1. Recebe CreateTransactionCommand (dados brutos do mundo externo)
     *   2. Constrói Transaction (entidade de domínio) — as validações
     *      acontecem DENTRO do construtor da Transaction. Se os dados
     *      forem inválidos, Transaction lança DomainException antes
     *      de ser criada. O service nem precisa validar nada.
     *   3. Delega a persistência ao repositório (via port out)
     *   4. Retorna a Transaction salva (com ID confirmado)
     */
    @Override
    public Transaction execute(CreateTransactionCommand command) {

        // Cria a entidade de domínio passando os dados do command.
        // Se qualquer valor for inválido (amount <= 0, description em branco, etc.)
        // o construtor de Transaction lança DomainException automaticamente.
        Transaction transaction = new Transaction(
                command.getUserId(),
                command.getDescription(),
                command.getAmount(),
                command.getType(),
                command.getCategory(),
                command.getDate()
        );

        // Persiste via port de saída e retorna o resultado.
        // "repository" aqui é a interface. Quem executa de verdade
        // é o TransactionJpaAdapter (injetado pelo Spring).
        return repository.save(transaction);
    }

    // =========================================================
    // CASO DE USO 2: Listar Transações
    // =========================================================

    /**
     * Retorna todas as transações registradas.
     *
     * FLUXO:
     *   1. Delega busca ao repositório
     *   2. Retorna lista (pode ser vazia)
     *
     * Sem lógica extra por enquanto. No futuro poderia aplicar
     * filtros, paginação, ordenação — tudo aqui, sem tocar adapters.
     */
    @Override
    public List<Transaction> listAll(UUID userId) {
        return repository.findAllByUserId(userId);
    }

    // =========================================================
    // CASO DE USO 3: Calcular Saldo
    // =========================================================

    /**
     * Calcula o saldo atual: soma de receitas - soma de despesas.
     *
     * FLUXO:
     *   1. Busca todas as transações
     *   2. Separa em INCOME e EXPENSE usando método de domínio (isIncome/isExpense)
     *   3. Soma cada grupo
     *   4. Retorna receitas - despesas (pode ser negativo)
     *
     * DECISÃO: a lógica de cálculo fica no APPLICATION, não no domínio puro.
     *   Poderia estar em Transaction como método estático, mas o cálculo
     *   de saldo é uma operação sobre uma COLEÇÃO de transações —
     *   isso é responsabilidade do Use Case, não da entidade.
     */
    @Override
    public BigDecimal calculate(UUID userId) {
        List<Transaction> all = repository.findAllByUserId(userId);

        // Soma todas as receitas
        BigDecimal totalIncome = all.stream()
                .filter(Transaction::isIncome)                // método do domínio
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);    // começa em 0.00

        // Soma todas as despesas
        BigDecimal totalExpense = all.stream()
                .filter(Transaction::isExpense)               // método do domínio
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saldo = receitas - despesas
        return totalIncome.subtract(totalExpense);
    }
}
