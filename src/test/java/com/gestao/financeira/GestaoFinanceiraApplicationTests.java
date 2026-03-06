package com.gestao.financeira;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste gerado pelo Spring Initializr.
 * Apenas verifica que o contexto Spring sobe sem erros.
 *
 * @AutoConfigureTestDatabase(replace = Replace.ANY):
 *   Substitui a datasource configurada (PostgreSQL) por H2 em memória.
 *   Isso permite rodar o teste sem ter um PostgreSQL rodando.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GestaoFinanceiraApplicationTests {

    @Test
    void contextLoads() {
        // Verifica se o contexto Spring sobe corretamente com H2
    }
}
