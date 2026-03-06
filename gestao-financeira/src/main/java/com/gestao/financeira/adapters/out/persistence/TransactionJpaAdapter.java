package com.gestao.financeira.adapters.out.persistence;

import com.gestao.financeira.adapters.out.persistence.entity.TransactionJpaEntity;
import com.gestao.financeira.adapters.out.persistence.mapper.TransactionPersistenceMapper;
import com.gestao.financeira.adapters.out.persistence.repository.TransactionJpaRepository;
import com.gestao.financeira.domain.model.Transaction;
import com.gestao.financeira.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter de saída: implementa a persistência de transações via JPA.
 *
 * CAMADA: Adapter OUT (adapters/out/persistence)
 * POR QUE EXISTE: Implementa o contrato definido pelo domínio
 *   (TransactionRepositoryPort) usando tecnologia JPA/Hibernate/PostgreSQL.
 *   É a "cola" entre o mundo do domínio e o mundo do banco de dados.
 *
 * IMPLEMENTA: TransactionRepositoryPort (port de saída do domínio)
 *   → O TransactionService chama esta classe sem saber que ela existe.
 *     Para o Service, ele está chamando uma interface.
 *     Para o Spring, este @Component é o candidato a ser injetado.
 *
 * USA:
 *   - TransactionJpaRepository: operações SQL via Spring Data
 *   - TransactionPersistenceMapper: conversão domínio ↔ JPA entity
 *
 * @Component: registra este bean no contexto Spring para ser injetado
 *   via BeanConfiguration no TransactionService.
 *
 * DEPENDÊNCIAS PERMITIDAS: domain.port.out, adapter persistence (entity, mapper, repository)
 * NÃO CONHECE: TransactionService, Controllers, DTOs
 */
@Component
public class TransactionJpaAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;

    public TransactionJpaAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Persiste uma transação no banco de dados.
     *
     * FLUXO:
     *   1. Recebe Transaction (domínio) — sem anotações JPA
     *   2. Converte para TransactionJpaEntity (JPA) via Mapper
     *   3. Persiste via Spring Data JPA (gera INSERT ou UPDATE)
     *   4. Converte resultado de volta para Transaction (domínio)
     *   5. Retorna Transaction salva
     *
     * Por que converter de volta após salvar?
     *   Em cenários futuros com @GeneratedValue ou triggers no banco,
     *   o objeto retornado pelo JPA pode ter campos diferentes do enviado.
     *   Converter sempre garante consistência.
     */
    @Override
    public Transaction save(Transaction transaction) {
        // domínio → JPA
        TransactionJpaEntity entity = TransactionPersistenceMapper.toJpaEntity(transaction);

        // JPA save: executa INSERT (novo) ou UPDATE (existente)
        TransactionJpaEntity saved = jpaRepository.save(entity);

        // JPA → domínio
        return TransactionPersistenceMapper.toDomain(saved);
    }

    /**
     * Busca todas as transações do banco de dados.
     *
     * FLUXO:
     *   1. Busca lista de TransactionJpaEntity via Spring Data
     *   2. Converte cada elemento para Transaction (domínio) via stream
     *   3. Retorna lista de Transaction
     *
     * O domínio recebe Transaction — nunca sabe que existiu JPA.
     */
    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll()          // List<TransactionJpaEntity>
                .stream()
                .map(TransactionPersistenceMapper::toDomain)  // cada JPA entity → domínio
                .toList();                                     // List<Transaction>
    }
}
