package com.gestao.financeira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Spring Boot.
 *
 * Esta classe pertence à camada de INFRASTRUCTURE.
 * Ela é responsável por inicializar o framework e
 * fazer o "bootstrap" de toda a aplicação.
 *
 * O domínio NÃO sabe que esta classe existe.
 */
@SpringBootApplication
public class GestaoFinanceiraApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoFinanceiraApplication.class, args);
    }
}
