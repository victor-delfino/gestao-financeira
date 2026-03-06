package com.gestao.financeira.adapters.in.web.dto;

import com.gestao.financeira.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para criação de transação.
 *
 * CAMADA: Adapter IN (adapters/in/web/dto)
 * POR QUE EXISTE: Representa o JSON que o cliente envia via HTTP POST.
 *   É o "formulário" que o mundo externo preenche.
 *   Não tem ID — o sistema gera o ID internamente.
 *
 * DECISÃO — Validação aqui (@NotNull, @NotBlank):
 *   Validamos o formato/presença dos dados na borda da aplicação (controller).
 *   A entidade Transaction valida as REGRAS DE NEGÓCIO (ex: amount > 0).
 *   São responsabilidades diferentes:
 *     - DTO valida: "o campo foi enviado e está no formato correto?"
 *     - Domain valida: "o valor faz sentido para o negócio?"
 *
 * DEPENDÊNCIAS PERMITIDAS: jakarta.validation, domain.model (para o enum)
 */
public class CreateTransactionRequest {

    @NotBlank(message = "A descrição é obrigatória.")
    private String description;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal amount;

    @NotNull(message = "O tipo é obrigatório (INCOME ou EXPENSE).")
    private TransactionType type;

    @NotBlank(message = "A categoria é obrigatória.")
    private String category;

    @NotNull(message = "A data é obrigatória.")
    private LocalDate date;

    // ── Construtor vazio exigido pelo Jackson (deserialização JSON) ──
    public CreateTransactionRequest() {}

    public CreateTransactionRequest(String description,
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

    public String getDescription()      { return description; }
    public BigDecimal getAmount()        { return amount; }
    public TransactionType getType()     { return type; }
    public String getCategory()          { return category; }
    public LocalDate getDate()           { return date; }
}
