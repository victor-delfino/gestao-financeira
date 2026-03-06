package com.gestao.financeira.adapters.in.web;

import com.gestao.financeira.adapters.in.web.dto.CreateTransactionRequest;
import com.gestao.financeira.adapters.in.web.dto.TransactionResponse;
import com.gestao.financeira.application.service.CreateTransactionCommand;
import com.gestao.financeira.domain.model.Transaction;
import com.gestao.financeira.domain.port.in.CalculateBalanceUseCase;
import com.gestao.financeira.domain.port.in.CreateTransactionUseCase;
import com.gestao.financeira.domain.port.in.ListTransactionsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para operações de transação financeira.
 *
 * CAMADA: Adapter IN (adapters/in/web)
 * POR QUE EXISTE: Traduz requisições HTTP para chamadas aos Use Cases.
 *   É a "porta de entrada" do sistema via protocolo HTTP.
 *
 * RESPONSABILIDADE ÚNICA: traduzir HTTP ↔ domínio.
 *   Nada de lógica de negócio aqui. Se houver um if/else de regra
 *   de negócio neste arquivo, é sinal de que algo está errado.
 *
 * CONHECE:
 *   - DTOs (CreateTransactionRequest, TransactionResponse)
 *   - Ports de entrada (interfaces — NÃO a implementação concreta)
 *   - CreateTransactionCommand (objeto interno de Application)
 *   - Anotações do Spring Web (@RestController, @PostMapping, etc.)
 *
 * NÃO CONHECE:
 *   - TransactionService (implementação concreta)
 *   - TransactionJpaAdapter (banco de dados)
 *   - Qualquer detalhe de persistência
 *
 * INJEÇÃO DE DEPENDÊNCIA:
 *   O construtor recebe as INTERFACES (ports), nunca as implementações.
 *   O Spring (via BeanConfiguration) injeta o TransactionService aqui,
 *   mas o Controller não sabe disso.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    // ── Depende das INTERFACES (ports), não do Service ──
    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final CalculateBalanceUseCase calculateBalanceUseCase;

    /**
     * Injeção via construtor — padrão recomendado no Spring moderno.
     * Mais explícito que @Autowired, facilita testes (sem Spring necessário).
     */
    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                  ListTransactionsUseCase listTransactionsUseCase,
                                  CalculateBalanceUseCase calculateBalanceUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.calculateBalanceUseCase = calculateBalanceUseCase;
    }

    // =========================================================
    // POST /api/transactions
    // Cria uma nova transação financeira
    // =========================================================

    /**
     * Recebe um JSON com os dados da transação e a persiste.
     *
     * FLUXO:
     *   1. @Valid dispara as validações do DTO (campos obrigatórios, etc.)
     *   2. Converte DTO → Command (desacopla HTTP do domínio)
     *   3. Chama o Use Case (Port IN)
     *   4. Converte Transaction → Response DTO
     *   5. Retorna 201 Created com o corpo da transação criada
     *
     * Exemplo de body:
     * {
     *   "description": "Aluguel",
     *   "amount": 1500.00,
     *   "type": "EXPENSE",
     *   "category": "Moradia",
     *   "date": "2026-03-06"
     * }
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody @Valid CreateTransactionRequest request) {

        // PASSO 1: DTO → Command
        // Aqui o Adapter traduz o "idioma HTTP" para o "idioma interno".
        // O Use Case só entende Command, nunca sabe que existiu um DTO.
        CreateTransactionCommand command = new CreateTransactionCommand(
                request.getDescription(),
                request.getAmount(),
                request.getType(),
                request.getCategory(),
                request.getDate()
        );

        // PASSO 2: Chama o Use Case via Port (interface)
        // Se os dados violarem regras de negócio, Transaction lança
        // DomainException aqui — capturada pelo GlobalExceptionHandler.
        Transaction created = createTransactionUseCase.execute(command);

        // PASSO 3: Entidade de domínio → DTO de resposta
        TransactionResponse response = TransactionResponse.from(created);

        // PASSO 4: Retorna HTTP 201 Created com o corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================
    // GET /api/transactions
    // Lista todas as transações registradas
    // =========================================================

    /**
     * Retorna a lista de todas as transações.
     * Responde com HTTP 200 OK e um array JSON.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listAll() {

        List<TransactionResponse> responses = listTransactionsUseCase.listAll()
                .stream()
                .map(TransactionResponse::from)   // Transaction → DTO para cada item
                .toList();

        return ResponseEntity.ok(responses);
    }

    // =========================================================
    // GET /api/transactions/balance
    // Retorna o saldo atual (receitas - despesas)
    // =========================================================

    /**
     * Calcula e retorna o saldo financeiro atual.
     * Responde com HTTP 200 OK e o valor numérico.
     */
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        BigDecimal balance = calculateBalanceUseCase.calculate();
        return ResponseEntity.ok(balance);
    }
}
