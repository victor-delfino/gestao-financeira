package com.gestao.financeira.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financeira.adapters.in.web.dto.CreateTransactionRequest;
import com.gestao.financeira.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  TESTE DE INTEGRAÇÃO — Spring Boot completo + H2 em memória
 * ════════════════════════════════════════════════════════════════════
 *
 *  Aqui testamos o fluxo completo:
 *  HTTP Request → Controller → Service → Repository → H2 → Response
 *
 *  Diferente do teste unitário, aqui o contexto Spring sobe de verdade.
 *  Isso é mais lento (segundos), mas valida a integração entre camadas.
 *
 *  @SpringBootTest
 *    Sobe o contexto Spring completo. Todos os Beans são criados.
 *
 *  @AutoConfigureMockMvc
 *    Configura o MockMvc automaticamente. MockMvc simula requisições HTTP
 *    sem precisar de um servidor real (mais rápido que TestRestTemplate).
 *
 *  @AutoConfigureTestDatabase(replace = Replace.ANY)
 *    Mais confiável que @ActiveProfiles: substitui qualquer datasource
 *    configurada (PostgreSQL, MySQL, etc.) por H2 em memória sem
 *    depender de arquivo de profile externo. É a abordagem oficial Spring.
 *
 *  @Transactional
 *    Cada teste roda dentro de uma transação que é revertida (rollback)
 *    ao final. Garante isolamento total sem precisar deletar dados ou
 *    usar @DirtiesContext (que recria o contexto inteiro a cada teste).
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@DisplayName("TransactionController — testes de integração")
class TransactionControllerTest {

    // MockMvc: simula chamadas HTTP reais para os endpoints
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper: converte objetos Java em JSON (serialização)
    @Autowired
    private ObjectMapper objectMapper;

    // ─── helper para criar request válida ─────────────────────────────
    private CreateTransactionRequest buildValidRequest(String description,
                                                       double amount,
                                                       TransactionType type) {
        return new CreateTransactionRequest(
            description,
            BigDecimal.valueOf(amount),
            type,
            "Categoria",
            LocalDate.of(2024, 1, 15)
        );
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 1: POST /api/transactions
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/transactions")
    class PostTransactions {

        @Test
        @DisplayName("deve criar transação e retornar 201 com o recurso criado")
        void deveCriarTransacaoComSucesso() throws Exception {
            CreateTransactionRequest request = buildValidRequest(
                "Salário", 5000.00, TransactionType.INCOME
            );

            /*
             * mockMvc.perform(...): executa a requisição
             * post("/api/transactions"): método HTTP POST + endpoint
             * contentType(JSON): header Content-Type: application/json
             * content(json): corpo da requisição
             *
             * .andExpect(...): matchers que validam a resposta
             * status().isCreated(): HTTP 201
             * jsonPath("$.id"): campo "id" no JSON de resposta
             * jsonPath("$.description", is("Salário")): verifica valor
             */
            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description", is("Salário")))
                .andExpect(jsonPath("$.amount", is(5000.00)))
                .andExpect(jsonPath("$.type", is("INCOME")))
                .andExpect(jsonPath("$.category", is("Categoria")));
        }

        @Test
        @DisplayName("deve retornar 400 quando descrição está em branco")
        void deveRetornar400ParaDescricaoEmBranco() throws Exception {
            CreateTransactionRequest request = buildValidRequest("", 100.00, TransactionType.EXPENSE);

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando valor é zero")
        void deveRetornar400ParaValorZero() throws Exception {
            CreateTransactionRequest request = buildValidRequest("Despesa", 0.00, TransactionType.EXPENSE);

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando tipo é nulo")
        void deveRetornar400ParaTipoNulo() throws Exception {
            // Criamos o request sem tipo (nulo)
            CreateTransactionRequest request = new CreateTransactionRequest(
                "Teste", BigDecimal.valueOf(100.00), null, "Cat", LocalDate.now()
            );

            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando body está vazio")
        void deveRetornar400ParaBodyVazio() throws Exception {
            mockMvc.perform(post("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 2: GET /api/transactions
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/transactions")
    class GetTransactions {

        @Test
        @DisplayName("deve retornar lista vazia quando não há transações")
        void deveRetornarListaVazia() throws Exception {
            mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("deve retornar as transações criadas")
        void deveRetornarTransacoesCriadas() throws Exception {
            // Criamos 2 transações via POST
            criarTransacao("Salário", 5000.00, TransactionType.INCOME);
            criarTransacao("Aluguel", 1500.00, TransactionType.EXPENSE);

            // Listamos e verificamos que existem 2
            mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].description",
                    containsInAnyOrder("Salário", "Aluguel")));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grupo 3: GET /api/transactions/balance
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/transactions/balance")
    class GetBalance {

        @Test
        @DisplayName("deve retornar zero quando não há transações")
        void deveRetornarZeroSemTransacoes() throws Exception {
            mockMvc.perform(get("/api/transactions/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("deve calcular saldo corretamente")
        void deveCalcularSaldo() throws Exception {
            criarTransacao("Salário", 5000.00, TransactionType.INCOME);
            criarTransacao("Aluguel", 1500.00, TransactionType.EXPENSE);

            // Saldo esperado: 5000 - 1500 = 3500
            mockMvc.perform(get("/api/transactions/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("3500")));
        }

        @Test
        @DisplayName("deve retornar saldo negativo quando despesas superam receitas")
        void deveRetornarSaldoNegativo() throws Exception {
            criarTransacao("Renda", 1000.00, TransactionType.INCOME);
            criarTransacao("Gasto A", 800.00, TransactionType.EXPENSE);
            criarTransacao("Gasto B", 700.00, TransactionType.EXPENSE);

            // 1000 - 800 - 700 = -500
            mockMvc.perform(get("/api/transactions/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("-500")));
        }
    }

    // ─── método auxiliar ──────────────────────────────────────────────
    private void criarTransacao(String description, double amount,
                                TransactionType type) throws Exception {
        CreateTransactionRequest request = buildValidRequest(description, amount, type);
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }
}
