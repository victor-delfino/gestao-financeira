package com.gestao.financeira.domain.model;

import com.gestao.financeira.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  TESTE UNITÁRIO PURO — sem Spring, sem Mockito, sem banco
 * ════════════════════════════════════════════════════════════════════
 *
 *  O domínio é o coração da aplicação. Ele deve ser testável com
 *  Java puro, sem precisar subir nenhum container, contexto Spring
 *  ou banco de dados.
 *
 *  Se você precisar de um framework para testar seu domínio,
 *  é sinal de que o domínio está com acoplamento indevido.
 *
 *  Ferramentas usadas:
 *  - JUnit 5 (@Test, @Nested, @DisplayName)
 *  - AssertJ (assertThat, assertThatThrownBy) — sintaxe fluente e legível
 *
 *  O que testamos aqui:
 *  - Criação válida de Transaction
 *  - Cada invariante de negócio (validações do construtor)
 *  - Comportamento dos métodos isIncome() / isExpense()
 */
@DisplayName("Transaction — testes de domínio")
class TransactionTest {

    // ─── dados de exemplo ─────────────────────────────────────────────
    private static final String DESC      = "Salário";
    private static final BigDecimal VALUE = new BigDecimal("5000.00");
    private static final LocalDate  DATE  = LocalDate.of(2024, 1, 15);    private static final UUID USER_ID     = UUID.randomUUID();
    // ─────────────────────────────────────────────────────────────────
    //  Grupo 1: criação bem-sucedida
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Quando os dados são válidos")
    class QuandoDadosValidos {

        @Test
        @DisplayName("deve criar uma transação de entrada com todos os campos")
        void deveCriarTransacaoDeEntrada() {
            // ARRANGE + ACT
            // O construtor de Transaction é chamado com dados válidos.
            // Se lançar exceção, o teste falha aqui.
            Transaction transaction = new Transaction(
                USER_ID, DESC, VALUE, TransactionType.INCOME, "Trabalho", DATE
            );

            // ASSERT — verificamos cada atributo individualmente
            assertThat(transaction.getId()).isNotNull();          // UUID gerado automaticamente
            assertThat(transaction.getUserId()).isEqualTo(USER_ID);
            assertThat(transaction.getDescription()).isEqualTo(DESC);
            assertThat(transaction.getAmount()).isEqualByComparingTo(VALUE);
            assertThat(transaction.getType()).isEqualTo(TransactionType.INCOME);
            assertThat(transaction.getCategory()).isEqualTo("Trabalho");
            assertThat(transaction.getDate()).isEqualTo(DATE);
        }

        @Test
        @DisplayName("deve criar uma transação de saída")
        void deveCriarTransacaoDeSaida() {
            Transaction transaction = new Transaction(
                USER_ID, "Aluguel", new BigDecimal("1500.00"), TransactionType.EXPENSE, "Moradia", DATE
            );

            assertThat(transaction.getType()).isEqualTo(TransactionType.EXPENSE);
        }

        @Test
        @DisplayName("deve reconhecer corretamente isIncome()")
        void deveIdentificarEntrada() {
            Transaction income = new Transaction(
                USER_ID, DESC, VALUE, TransactionType.INCOME, "Trabalho", DATE
            );

            // isIncome() e isExpense() são métodos do domínio que encapsulam a lógica
            // de verificação de tipo — nunca compare o enum diretamente fora do domínio
            assertThat(income.isIncome()).isTrue();
            assertThat(income.isExpense()).isFalse();
        }

        @Test
        @DisplayName("deve reconhecer corretamente isExpense()")
        void deveIdentificarSaida() {
            Transaction expense = new Transaction(
                USER_ID, "Mercado", new BigDecimal("300.00"), TransactionType.EXPENSE, "Alimentação", DATE
            );

            assertThat(expense.isExpense()).isTrue();
            assertThat(expense.isIncome()).isFalse();
        }

        @Test
        @DisplayName("deve reconstruir transação existente com ID fornecido")
        void deveReconstruirTransacaoExistente() {
            // Este construtor é usado ao buscar do banco de dados.
            // O ID vem do banco, não é gerado novamente.
            UUID id = UUID.randomUUID();

            Transaction transaction = new Transaction(
                id, USER_ID, DESC, VALUE, TransactionType.INCOME, "Trabalho", DATE
            );

            assertThat(transaction.getId()).isEqualTo(id);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 2: invariantes — o domínio deve rejeitar dados inválidos
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Quando os dados são inválidos")
    class QuandoDadosInvalidos {

        /**
         * assertThatThrownBy: verifica que o código dentro do lambda
         * LANÇA uma exceção do tipo especificado.
         * É mais expressivo que try/catch em testes.
         */

        @Test
        @DisplayName("deve lançar DomainException quando descrição é nula")
        void deveLancarExcecaoParaDescricaoNula() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, null, VALUE, TransactionType.INCOME, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("descrição");
        }

        @Test
        @DisplayName("deve lançar DomainException quando descrição está vazia")
        void deveLancarExcecaoParaDescricaoVazia() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, "   ", VALUE, TransactionType.INCOME, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("descrição");
        }

        @Test
        @DisplayName("deve lançar DomainException quando valor é nulo")
        void deveLancarExcecaoParaValorNulo() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, DESC, null, TransactionType.INCOME, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("valor");
        }

        @Test
        @DisplayName("deve lançar DomainException quando valor é zero")
        void deveLancarExcecaoParaValorZero() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, DESC, BigDecimal.ZERO, TransactionType.INCOME, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("valor");
        }

        @Test
        @DisplayName("deve lançar DomainException quando valor é negativo")
        void deveLancarExcecaoParaValorNegativo() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, DESC, new BigDecimal("-1.00"), TransactionType.INCOME, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("valor");
        }

        @Test
        @DisplayName("deve lançar DomainException quando tipo é nulo")
        void deveLancarExcecaoParaTipoNulo() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, DESC, VALUE, null, "Trabalho", DATE)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("tipo");
        }

        @Test
        @DisplayName("deve lançar DomainException quando data é nula")
        void deveLancarExcecaoParaDataNula() {
            assertThatThrownBy(() ->
                new Transaction(USER_ID, DESC, VALUE, TransactionType.INCOME, "Trabalho", null)
            )
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("data");
        }
    }
}
