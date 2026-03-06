package com.gestao.financeira.adapters.out.persistence;

import com.gestao.financeira.adapters.out.persistence.mapper.UserPersistenceMapper;
import com.gestao.financeira.adapters.out.persistence.repository.UserJpaRepository;
import com.gestao.financeira.domain.model.User;
import com.gestao.financeira.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter de saída — implementação concreta do UserRepositoryPort.
 *
 * CAMADA: Adapter OUT (persistência)
 * IMPLEMENTA: UserRepositoryPort (domínio)
 * USA: UserJpaRepository (Spring Data) + UserPersistenceMapper
 *
 * Segue o mesmo padrão do TransactionJpaAdapter:
 *   1. Recebe entidade de DOMÍNIO (User)
 *   2. Converte para JPA (UserJpaEntity)
 *   3. Usa Spring Data para persistir
 *   4. Converte de volta para domínio e retorna
 */
@Component
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserJpaAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        var jpaEntity = UserPersistenceMapper.toJpaEntity(user);
        var saved = jpaRepository.save(jpaEntity);
        return UserPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
