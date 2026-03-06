package com.gestao.financeira.application.service;

import com.gestao.financeira.domain.exception.DomainException;
import com.gestao.financeira.domain.model.Transaction;
import com.gestao.financeira.domain.model.TransactionType;
import com.gestao.financeira.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  TESTE UNITÁRIO COM MOCKITO — sem Spring, sem banco de dados
 * ════════════════════════════════════════════════════════════════════
 *
 *  Testamos o TransactionService isolado do mundo externo.
 *  O Port OUT (TransactionRepositoryPort) é SUBSTITUÍDO por um Mock.
 *
 *  Por que usar Mock?
 *  O TransactionService depende de TransactionRepositoryPort (interface).
 *  Em testes, não queremos testar a implementação JPA, queremos
 *  testar APENAS a lógica da regra de negócio do Service.
 *
 *  Um Mock é um "dublê de filmagem":
 *  - Ele implementa a interface
 *  - Você programa o comportamento: "quando chamar save(), retorna X"
 *  - Você verifica se foi chamado: "verify que save() foi chamado 1 vez"
 *
 *  @ExtendWith(MockitoExtension.class): habilita Mockito sem Spring
 *  @Mock: cria o mock automático
 *  @InjectMocks: cria TransactionService e injeta os mocks no construtor
 *
 *  ATENÇÃO: TransactionService recebe o repositório pelo CONSTRUTOR.
 *  Isso é Dependency Injection via construtor — permite que o Mockito
 *  injete o mock facilmente, sem precisar de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService — testes de regra de negócio")
class TransactionServiceTest {

    // @Mock cria uma implementação "falsa" da interface
    // controlada pelo Mockito
    @Mock
    private TransactionRepositoryPort repository;

    // @InjectMocks cria TransactionService chamando o construtor
    // e passando os @Mock acima como argumento
    @InjectMocks
    private TransactionService service;

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 1: execute() — criar transação
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("execute() — criar nova transação")
    class Execute {

        private CreateTransactionCommand validCommand;
        private final UUID userId = UUID.randomUUID();

        @BeforeEach
        void setUp() {
            validCommand = new CreateTransactionCommand(
                userId,
                "Salário",
                new BigDecimal("5000.00"),
                TransactionType.INCOME,
                "Trabalho",
                LocalDate.of(2024, 1, 15)
            );
        }

        @Test
        @DisplayName("deve salvar a transação e retornar o resultado")
        void deveSalvarTransacao() {
            // ARRANGE
            // when(...).thenReturn(...) programa o mock para retornar um valor
            // específico quando o método `save` for chamado com qualquer Transaction.
            // any(Transaction.class) = "qualquer Transaction, não importa qual"
            Transaction savedTransaction = new Transaction(
                userId, "Salário", new BigDecimal("5000.00"),
                TransactionType.INCOME, "Trabalho", LocalDate.of(2024, 1, 15)
            );
            when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // ACT
            Transaction result = service.execute(validCommand);

            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.getDescription()).isEqualTo("Salário");
            assertThat(result.getAmount()).isEqualByComparingTo("5000.00");
            assertThat(result.getType()).isEqualTo(TransactionType.INCOME);

            // verify: confirma que repository.save() foi chamado EXATAMENTE 1 vez
            // Isso garante que o service não está bypassando a persistência
            verify(repository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("não deve chamar repository quando comando tem dados inválidos")
        void naoDeveChamarRepositorySeInvalido() {
            // Um comando com descrição em branco deve ser rejeitado pelo domínio
            // ANTES de chegar no repositório
            CreateTransactionCommand invalidCommand = new CreateTransactionCommand(
                userId,
                "", // descrição vazia — viola invariante de domínio
                new BigDecimal("100.00"),
                TransactionType.EXPENSE,
                "Teste",
                LocalDate.now()
            );

            // ASSERT que lança exceção de domínio
            assertThatThrownBy(() -> service.execute(invalidCommand))
                .isInstanceOf(DomainException.class);

            // verify com verifyNoInteractions: o repositório NUNCA deve ser chamado
            // se a criação da entidade de domínio falhou
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("deve repassar exceção do repositório")
        void deveRepassarExcecaoDoRepositorio() {
            // Programa o mock para LANÇAR uma exceção ao invés de retornar um valor
            when(repository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Erro de banco de dados"));

            assertThatThrownBy(() -> service.execute(validCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro de banco de dados");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 2: listAll() — listar transações
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("listAll() — listar todas as transações")
    class ListAll {

        private final UUID userId = UUID.randomUUID();

        @Test
        @DisplayName("deve retornar lista do repositório")
        void deveRetornarListaDoRepositorio() {
            // ARRANGE — programa o mock para retornar 2 transações
            List<Transaction> mockTransactions = List.of(
                new Transaction(userId, "Salário", new BigDecimal("5000.00"),
                    TransactionType.INCOME, "Trabalho", LocalDate.now()),
                new Transaction(userId, "Aluguel", new BigDecimal("1500.00"),
                    TransactionType.EXPENSE, "Moradia", LocalDate.now())
            );
            when(repository.findAllByUserId(userId)).thenReturn(mockTransactions);

            // ACT
            List<Transaction> result = service.listAll(userId);

            // ASSERT
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Transaction::getDescription)
                .containsExactly("Salário", "Aluguel");

            verify(repository, times(1)).findAllByUserId(userId);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há transações")
        void deveRetornarListaVazia() {
            when(repository.findAllByUserId(userId)).thenReturn(List.of());

            List<Transaction> result = service.listAll(userId);

            assertThat(result).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 3: calculate() — saldo
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("calculate() — calcular saldo")
    class Calculate {

        private final UUID userId = UUID.randomUUID();

        @Test
        @DisplayName("deve retornar saldo positivo quando receitas > despesas")
        void deveRetornarSaldoPositivo() {
            when(repository.findAllByUserId(userId)).thenReturn(List.of(
                new Transaction(userId, "Salário", new BigDecimal("5000.00"),
                    TransactionType.INCOME, "Trabalho", LocalDate.now()),
                new Transaction(userId, "Aluguel", new BigDecimal("1500.00"),
                    TransactionType.EXPENSE, "Moradia", LocalDate.now()),
                new Transaction(userId, "Freelance", new BigDecimal("2000.00"),
                    TransactionType.INCOME, "Trabalho", LocalDate.now())
            ));

            BigDecimal balance = service.calculate(userId);

            // 5000 + 2000 - 1500 = 5500
            assertThat(balance).isEqualByComparingTo("5500.00");
        }

        @Test
        @DisplayName("deve retornar saldo negativo quando despesas > receitas")
        void deveRetornarSaldoNegativo() {
            when(repository.findAllByUserId(userId)).thenReturn(List.of(
                new Transaction(userId, "Salário", new BigDecimal("1000.00"),
                    TransactionType.INCOME, "Trabalho", LocalDate.now()),
                new Transaction(userId, "Aluguel", new BigDecimal("1500.00"),
                    TransactionType.EXPENSE, "Moradia", LocalDate.now())
            ));

            BigDecimal balance = service.calculate(userId);

            // 1000 - 1500 = -500
            assertThat(balance).isEqualByComparingTo("-500.00");
        }

        @Test
        @DisplayName("deve retornar zero quando a lista está vazia")
        void deveRetornarZeroParaListaVazia() {
            when(repository.findAllByUserId(userId)).thenReturn(List.of());

            BigDecimal balance = service.calculate(userId);

            assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve somar apenas receitas e subtrair apenas despesas")
        void deveSomarReceitasESubtrairDespesas() {
            when(repository.findAllByUserId(userId)).thenReturn(List.of(
                new Transaction(userId, "Renda A", new BigDecimal("3000.00"),
                    TransactionType.INCOME, "Cat", LocalDate.now()),
                new Transaction(userId, "Renda B", new BigDecimal("2000.00"),
                    TransactionType.INCOME, "Cat", LocalDate.now()),
                new Transaction(userId, "Gasto A", new BigDecimal("500.00"),
                    TransactionType.EXPENSE, "Cat", LocalDate.now()),
                new Transaction(userId, "Gasto B", new BigDecimal("700.00"),
                    TransactionType.EXPENSE, "Cat", LocalDate.now())
            ));

            BigDecimal balance = service.calculate(userId);

            // (3000 + 2000) - (500 + 700) = 3800
            assertThat(balance).isEqualByComparingTo("3800.00");
        }
    }
}
