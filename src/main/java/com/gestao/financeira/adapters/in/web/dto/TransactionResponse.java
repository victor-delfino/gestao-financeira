package com.gestao.financeira.adapters.in.web.dto;

import com.gestao.financeira.domain.model.Transaction;
import com.gestao.financeira.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de saída para representar uma transação na resposta HTTP.
 *
 * CAMADA: Adapter IN (adapters/in/web/dto)
 * POR QUE EXISTE: Representa o JSON que o sistema devolve ao cliente.
 *   Sempre inclui o ID (gerado pelo sistema).
 *   Pode ter campos extras que o domínio não precisa guardar
 *   (ex: mensagens formatadas, links HATEOAS no futuro).
 *
 * DECISÃO — método estático from(Transaction):
 *   Converte Transaction (domínio) → TransactionResponse (DTO).
 *   A lógica de conversão fica no próprio DTO de resposta —
 *   simples, sem precisar de uma classe Mapper separada neste momento.
 *   Se a conversão crescer (muitos campos calculados), extrai para mapper.
 *
 * DEPENDÊNCIAS PERMITIDAS: domain.model (para conversão)
 */
public class TransactionResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;

    // ── Construtor vazio para serialização Jackson ──
    public TransactionResponse() {}

    private TransactionResponse(UUID id,
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

    /**
     * Converte uma entidade de domínio Transaction para o DTO de resposta.
     *
     * Este é o ponto onde o Adapter traduz do domínio para o mundo externo.
     * O Controller chama este método antes de retornar a ResponseEntity.
     *
     * @param transaction entidade de domínio
     * @return DTO pronto para ser serializado como JSON
     */
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDate()
        );
    }

    public UUID getId()               { return id; }
    public String getDescription()    { return description; }
    public BigDecimal getAmount()     { return amount; }
    public TransactionType getType()  { return type; }
    public String getCategory()       { return category; }
    public LocalDate getDate()        { return date; }
}
