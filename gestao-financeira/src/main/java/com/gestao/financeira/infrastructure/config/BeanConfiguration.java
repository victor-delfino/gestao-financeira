package com.gestao.financeira.infrastructure.config;

import com.gestao.financeira.application.service.TransactionService;
import com.gestao.financeira.application.service.UserService;
import com.gestao.financeira.domain.port.out.PasswordEncoderPort;
import com.gestao.financeira.domain.port.out.TokenPort;
import com.gestao.financeira.domain.port.out.TransactionRepositoryPort;
import com.gestao.financeira.domain.port.out.UserRepositoryPort;
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
     * Bean ÚNICO do TransactionService — implementa os três ports de entrada:
     *   CreateTransactionUseCase, ListTransactionsUseCase, CalculateBalanceUseCase.
     *
     * Anteriormente criávamos 3 beans separados, mas como TransactionService
     * implementa os 3 interfaces, Spring encontrava múltiplos candidatos.
     * Com um bean único, a resolução é direta:
     *   Controller pede CreateTransactionUseCase → encontra transactionService → OK
     */
    @Bean
    public TransactionService transactionService(
            TransactionRepositoryPort repository) {
        return new TransactionService(repository);
    }

    // ═════════════════════════════════════════════════════════════════
    //  USER SERVICE — Use Cases de autenticação
    // ═════════════════════════════════════════════════════════════════

    /**
     * Bean ÚNICO do UserService — implementa RegisterUserUseCase e AuthenticateUserUseCase.
     *
     * Dependências injetadas pelo Spring:
     *   - UserRepositoryPort ← UserJpaAdapter (@Component)
     *   - PasswordEncoderPort ← BcryptPasswordEncoderAdapter (@Component)
     *   - TokenPort ← JwtTokenProvider (@Component)
     */
    @Bean
    public UserService userService(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenPort tokenPort) {
        return new UserService(userRepository, passwordEncoder, tokenPort);
    }
}
