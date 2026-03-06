package com.gestao.financeira.adapters.out.persistence.mapper;

import com.gestao.financeira.adapters.out.persistence.entity.TransactionJpaEntity;
import com.gestao.financeira.domain.model.Transaction;

/**
 * Conversor entre entidade de domínio e entidade JPA.
 *
 * CAMADA: Adapter OUT (adapters/out/persistence/mapper)
 * POR QUE EXISTE: Domínio e banco de dados têm representações diferentes
 *   do mesmo conceito. Este mapper traduz entre os dois mundos.
 *   Mantém o domínio isolado do JPA e o JPA isolado do domínio.
 *
 * PADRÃO: classe com métodos estáticos — sem estado, só conversão pura.
 *   Não precisa ser instanciada, não precisa de Spring.
 *
 * RESPONSABILIDADE:
 *   toJpaEntity(Transaction)      → quando vamos SALVAR no banco
 *   toDomain(TransactionJpaEntity) → quando LEMOS do banco
 *
 * DEPENDÊNCIAS PERMITIDAS: domain.model, adapter entity
 */
public class TransactionPersistenceMapper {

    // Classe utilitária — construtor privado para impedir instanciação
    private TransactionPersistenceMapper() {}

    /**
     * Converte entidade de DOMÍNIO → entidade JPA.
     * Chamado antes de salvar no banco: domínio → persistência.
     *
     * @param transaction entidade de domínio (com regras de negócio)
     * @return entidade JPA (com anotações de banco)
     */
    public static TransactionJpaEntity toJpaEntity(Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDate()
        );
    }

    /**
     * Converte entidade JPA → entidade de DOMÍNIO.
     * Chamado após ler do banco: persistência → domínio.
     *
     * Usa o construtor COMPLETO da Transaction (com ID) porque
     * a transação já existe — foi lida do banco, tem ID definido.
     * As validações do construtor ainda são executadas — garantia extra.
     *
     * @param entity entidade JPA (vinda do Hibernate)
     * @return entidade de domínio (válida, com regras de negócio)
     */
    public static Transaction toDomain(TransactionJpaEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getUserId(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getType(),
                entity.getCategory(),
                entity.getDate()
        );
    }
}
