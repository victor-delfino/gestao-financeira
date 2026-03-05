package com.gestao.financeira.domain.model;

import com.gestao.financeira.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade central do domínio financeiro.
 *
 * CAMADA: Domain
 * POR QUE EXISTE: Representa uma movimentação financeira (receita ou despesa).
 *   É o conceito mais importante do negócio. Todas as outras camadas
 *   giram em torno desta entidade.
 *
 * DECISÕES DE DESIGN:
 *   - Sem @Entity: esta classe NÃO é JPA. A entidade JPA existe separada,
 *     que estará adapters/out/persistence/entity/TransactionJpaEntity.java.
 *     Isso garante que o domínio não fica acoplado ao banco de dados.
 *
 *   - Sem Lombok: domínio é código que você lê e entende sem ferramentas.
 *     Lombok em entidades de domínio esconde comportamento e dificulta
 *     o entendimento do estado do objeto.
 *
 *   - BigDecimal para valor: double e float têm problemas de precisão com
 *     aritmética financeira. BigDecimal é o tipo correto para dinheiro.
 *
 *   - Construtor valida: um objeto Transaction só pode ser criado se
 *     todos os dados forem válidos. Isso é chamado de "invariante de domínio".
 *     Se estiver inválido, lança DomainException ANTES de criar o objeto.
 *
 *   - Sem setters públicos: após criado, os dados não mudam arbitrariamente.
 *     Se precisar alterar, será via método de negócio explícito (ex: update()).
 *
 * DEPENDÊNCIAS PERMITIDAS: apenas java.* e classes do próprio domain.
 */
public class Transaction {

    // =========================================================
    // ATRIBUTOS
    // Todos privados e final — imutáveis após a construção.
    // =========================================================

    private final UUID id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;
    private final LocalDate date;

    // =========================================================
    // CONSTRUTOR PRINCIPAL
    // Chamado quando criamos uma NOVA transação (sem ID ainda).
    // Gera o ID automaticamente.
    // =========================================================

    /**
     * Cria uma nova transação com ID gerado automaticamente.
     * Valida todos os campos antes de criar o objeto.
     *
     * @throws DomainException se qualquer regra de negócio for violada
     */
    public Transaction(String description,
                       BigDecimal amount,
                       TransactionType type,
                       String category,
                       LocalDate date) {
        this(UUID.randomUUID(), description, amount, type, category, date);
    }

    // =========================================================
    // CONSTRUTOR COMPLETO
    // Chamado quando RECONSTRUÍMOS uma transação do banco de dados
    // (o ID já existe, foi salvo anteriormente).
    // =========================================================

    /**
     * Reconstrói uma transação existente (vinda do banco de dados).
     * O adapter de persistência usa este construtor ao converter
     * TransactionJpaEntity → Transaction.
     *
     * @throws DomainException se qualquer regra de negócio for violada
     */
    public Transaction(UUID id,
                       String description,
                       BigDecimal amount,
                       TransactionType type,
                       String category,
                       LocalDate date) {

        // ── VALIDAÇÕES DE DOMÍNIO ─────────────────────────────────
        // Estas regras são INVARIANTES: sempre válidas, sem exceção.
        // Não importa se veio da API REST, do banco, de um teste...
        // A Transaction NUNCA aceita um estado inválido.

        if (description == null || description.isBlank()) {
            throw new DomainException("A descrição da transação é obrigatória.");
        }

        if (amount == null) {
            throw new DomainException("O valor da transação é obrigatório.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("O valor da transação deve ser maior que zero.");
        }

        if (type == null) {
            throw new DomainException("O tipo da transação é obrigatório (INCOME ou EXPENSE).");
        }

        if (category == null || category.isBlank()) {
            throw new DomainException("A categoria da transação é obrigatória.");
        }

        if (date == null) {
            throw new DomainException("A data da transação é obrigatória.");
        }
        // ── FIM DAS VALIDAÇÕES ────────────────────────────────────

        // Só chegamos aqui se TUDO for válido.
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    // =========================================================
    // GETTERS
    // Apenas leitura. Não há setters — o estado é imutável.
    // =========================================================

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    // =========================================================
    // MÉTODOS DE NEGÓCIO
    // A entidade pode ter comportamento além de guardar dados.
    // Aqui ficam as regras de negócio que pertencem à Transaction.
    // =========================================================

    /**
     * Verifica se esta transação é uma receita.
     * Método de negócio: encapsula a comparação com o enum.
     */
    public boolean isIncome() {
        return TransactionType.INCOME.equals(this.type);
    }

    /**
     * Verifica se esta transação é uma despesa.
     */
    public boolean isExpense() {
        return TransactionType.EXPENSE.equals(this.type);
    }

    // =========================================================
    // toString — útil para logs e debugging
    // =========================================================

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", date=" + date +
                '}';
    }
}
