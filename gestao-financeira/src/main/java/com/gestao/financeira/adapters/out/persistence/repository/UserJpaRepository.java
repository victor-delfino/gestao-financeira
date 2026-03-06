package com.gestao.financeira.adapters.out.persistence.repository;

import com.gestao.financeira.adapters.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para UserJpaEntity.
 *
 * CAMADA: Adapter OUT (persistência)
 *
 * O Spring Data gera a implementação automaticamente a partir
 * dos nomes dos métodos:
 *   - findByEmail → SELECT * FROM users WHERE email = ?
 *   - existsByEmail → SELECT COUNT(*) > 0 FROM users WHERE email = ?
 *
 * O JpaRepository já fornece: save(), findById(), findAll(), delete(), etc.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
