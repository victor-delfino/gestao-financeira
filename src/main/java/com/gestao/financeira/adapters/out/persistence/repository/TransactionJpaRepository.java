package com.gestao.financeira.adapters.out.persistence.repository;

import com.gestao.financeira.adapters.out.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositório Spring Data JPA para TransactionJpaEntity.
 *
 * CAMADA: Adapter OUT (adapters/out/persistence/repository)
 * POR QUE EXISTE: Fornece operações de banco de dados sem escrever SQL.
 *   O Spring Data gera a implementação automaticamente em tempo de execução.
 *
 * HERDA DE JpaRepository<TransactionJpaEntity, UUID>:
 *   - TransactionJpaEntity: tipo da entidade gerenciada
 *   - UUID: tipo do campo @Id
 *   Isso nos dá gratuitamente: save(), findById(), findAll(),
 *   delete(), count(), existsById(), etc.
 *
 * QUEM USA ESTA INTERFACE:
 *   TransactionJpaAdapter — o único consumidor.
 *   Note que o domínio (TransactionService) NUNCA chega até aqui.
 *   O Service conhece apenas TransactionRepositoryPort (interface de domínio).
 *
 * DEPENDÊNCIAS PERMITIDAS: spring.data.jpa, entity JPA
 */
public interface TransactionJpaRepository
        extends JpaRepository<TransactionJpaEntity, UUID> {

    // Métodos herdados do JpaRepository que usaremos:
    //   save(entity)     → INSERT ou UPDATE
    //   findAll()        → SELECT * FROM transactions
    //
    // No futuro, poderíamos adicionar queries customizadas aqui:
    //   List<TransactionJpaEntity> findByType(TransactionType type);
    //   List<TransactionJpaEntity> findByDateBetween(LocalDate start, LocalDate end);
}
