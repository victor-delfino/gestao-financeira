package com.gestao.financeira.infrastructure.config;

import com.gestao.financeira.application.service.TransactionService;
import com.gestao.financeira.domain.port.in.CalculateBalanceUseCase;
import com.gestao.financeira.domain.port.in.CreateTransactionUseCase;
import com.gestao.financeira.domain.port.in.ListTransactionsUseCase;
import com.gestao.financeira.domain.port.out.TransactionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans — conecta as peças da arquitetura hexagonal.
 *
 * CAMADA: Infrastructure (infrastructure/config)
 * POR QUE EXISTE: Na arquitetura hexagonal, as classes de Application
 *   (como TransactionService) NÃO têm @Service — intencionalmente.
 *   Isso as mantém livres de dependência com o Spring.
 *
 *   Então, como o Spring sabe como criar o TransactionService?
 *   Através desta classe @Configuration, que declara @Beans manualmente.
 *
 * O QUE ELA FAZ:
 *   1. Recebe TransactionRepositoryPort via parâmetro do @Bean
 *      (Spring injeta automaticamente o @Component que o implementa,
 *       ou seja, o TransactionJpaAdapter)
 *   2. Cria uma instância de TransactionService passando o repositório
 *   3. Registra o TransactionService como bean do tipo dos três ports:
 *      CreateTransactionUseCase, ListTransactionsUseCase, CalculateBalanceUseCase
 *
 * QUEM FAZ O WIRING:
 *   Spring encontra TransactionJpaAdapter (@Component) que implementa
 *   TransactionRepositoryPort e o injeta aqui no parâmetro do @Bean.
 *   Então cria TransactionService com esse adapter.
 *   O Controller recebe CreateTransactionUseCase — que é o TransactionService.
 *
 * VISUALIZAÇÃO:
 *   TransactionJpaAdapter (@Component)
 *         │ implementa TransactionRepositoryPort
 *         │
 *         ▼ Spring injeta aqui
 *   BeanConfiguration.transactionService(repo)
 *         │ cria new TransactionService(repo)
 *         │
 *         ▼ registra como
 *   CreateTransactionUseCase  ←── TransactionController usa este bean
 *   ListTransactionsUseCase   ←── TransactionController usa este bean
 *   CalculateBalanceUseCase   ←── TransactionController usa este bean
 */
@Configuration
public class BeanConfiguration {

    /**
     * Registra o TransactionService como implementação de CreateTransactionUseCase.
     * O Controller que precisa de CreateTransactionUseCase recebe este bean.
     */
    @Bean
    public CreateTransactionUseCase createTransactionUseCase(
            TransactionRepositoryPort repository) {
        return new TransactionService(repository);
    }

    /**
     * Registra o TransactionService como implementação de ListTransactionsUseCase.
     *
     * Note: cria uma nova instância de TransactionService aqui.
     * Alternativamente, poderíamos extrair a criação para um método auxiliar,
     * mas para fins didáticos fica explícito — cada Use Case tem seu bean.
     */
    @Bean
    public ListTransactionsUseCase listTransactionsUseCase(
            TransactionRepositoryPort repository) {
        return new TransactionService(repository);
    }

    /**
     * Registra o TransactionService como implementação de CalculateBalanceUseCase.
     */
    @Bean
    public CalculateBalanceUseCase calculateBalanceUseCase(
            TransactionRepositoryPort repository) {
        return new TransactionService(repository);
    }
}
