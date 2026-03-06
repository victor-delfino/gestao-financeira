package com.gestao.financeira.adapters.out.persistence.mapper;

import com.gestao.financeira.adapters.out.persistence.entity.UserJpaEntity;
import com.gestao.financeira.domain.model.User;

/**
 * Mapper bidirecional: User (domínio) ↔ UserJpaEntity (JPA).
 *
 * CAMADA: Adapter OUT (persistência)
 * Mesma filosofia do TransactionPersistenceMapper:
 *   - Converte de domínio para JPA antes de salvar
 *   - Converte de JPA para domínio ao buscar do banco
 */
public class UserPersistenceMapper {

    private UserPersistenceMapper() {
        // Classe utilitária — não instanciar
    }

    /**
     * Domínio → JPA (para salvar no banco)
     */
    public static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword()
        );
    }

    /**
     * JPA → Domínio (para retornar ao service/use case)
     * Usa o construtor completo do User (com ID) para reconstituir.
     */
    public static User toDomain(UserJpaEntity entity) {
        return new User(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPassword()
        );
    }
}
